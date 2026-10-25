package com.grunvald.sorm.syncdatascheduler.scheduler;

import com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SyncTaskMetrics;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import com.grunvald.sorm.util.DateUtils;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.reactivestreams.Publisher;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.FORCE_FIRST;
import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.NORMAL;
import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.ACTIVITY_LOG;
import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;
import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.USER;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.FINISHED;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SchedulerTest {

    private static final OffsetDateTime NOW = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    private SchedulerHelper helper;
    private SchedulerTaskRepository repository;
    private DateUtils date;
    private TransactionalOperator transactional;
    private SchedulerCoordinator coordinator;
    private SyncTaskMetrics metrics;

    @BeforeEach
    void setUp() {
        helper = mock(SchedulerHelper.class);
        repository = mock(SchedulerTaskRepository.class);
        date = mock(DateUtils.class);
        transactional = mock(TransactionalOperator.class);
        coordinator = mock(SchedulerCoordinator.class);
        metrics = mock(SyncTaskMetrics.class);
        when(metrics.startTaskObservation(any())).thenReturn(Observation.NOOP);
        when(helper.createRetry(any(), any())).thenReturn(Retry.max(1));
        when(date.now()).thenReturn(NOW);
        when(transactional.transactional(any(Mono.class)))
                .thenAnswer(inv -> Mono.from(inv.getArgument(0, Publisher.class)));
        when(coordinator.waitUploadFor(any())).thenReturn(Mono.empty());
        when(helper.delayByCron(any(), any())).thenReturn(Mono.never());
        // Scheduler.switchIfEmpty вычисляет alternate-ветку жадно при сборке цепочки,
        // поэтому add/update должны быть заглушены всегда. При повторном стаббинге
        // any() передаёт null, поэтому ответ обязан быть null-безопасным.
        when(repository.add(any())).thenAnswer(inv -> {
            SchedulerTask task = inv.getArgument(0);
            return task == null ? Mono.empty() : Mono.just(task);
        });
        when(repository.update(any())).thenAnswer(inv -> {
            SchedulerTask task = inv.getArgument(0);
            return task == null ? Mono.empty() : Mono.just(task);
        });
    }

    @Test
    void start_withoutFirstTask_onlyFirstMode_signalsReady() {
        var config = config();
        config.setFirstStart(null);
        config.setMode(ONLY_FIRST);

        scheduler(config).start();

        verify(coordinator).signalReady(ACTIVITY_LOG);
        verifyNoInteractions(repository);
    }

    @Test
    void start_waitsForDependencies_thenSignalsReady() {
        var config = config();
        config.setFirstStart(null);
        config.setMode(ONLY_FIRST);

        scheduler(config, Set.of(CLIENT, USER)).start();

        verify(coordinator).waitUploadFor(CLIENT);
        verify(coordinator).waitUploadFor(USER);
        verify(coordinator).signalReady(ACTIVITY_LOG);
    }

    @Test
    void start_runsFirstTask_executesStepUpdatesTaskAndSignalsReady() {
        var config = config();
        config.setMode(ONLY_FIRST);
        var task = new SchedulerTask(ACTIVITY_LOG, NOW);
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(task));
        var scheduler = scheduler(config, step("d1", false, 10, "c1"));

        scheduler.start();

        verify(coordinator).signalReady(ACTIVITY_LOG);
        assertEquals(List.of(0L), scheduler.getOffsets());
        assertEquals(1, scheduler.getCursors().size());
        assertNull(scheduler.getCursors().getFirst());

        var captor = ArgumentCaptor.forClass(SchedulerTask.class);
        verify(repository).update(captor.capture());
        var updated = captor.getValue();
        assertEquals(10L, updated.getOffset());
        assertEquals("c1", updated.getCursor());
        assertEquals(FINISHED, updated.getStatus());
        assertEquals(NOW, updated.getFinishAt());
    }

    @Test
    void start_stepFailsOnce_retriesAndCompletes() {
        var config = config();
        config.setMode(ONLY_FIRST);
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(new SchedulerTask(ACTIVITY_LOG, NOW)));
        var scheduler = scheduler(config, new RuntimeException("first fail"), step("d1", false, 10, "c1"));

        scheduler.start();

        verify(coordinator).signalReady(ACTIVITY_LOG);
        verify(helper).handleError(any(), eq(ACTIVITY_LOG));
        assertEquals(List.of(0L, 0L), scheduler.getOffsets());
    }

    @Test
    void start_continueAfterErrorDisabled_stopsAndCallsHandleFailure() {
        var config = config();
        config.setFirstStart(new SchedulerPropertiesConfig.FirstStart(List.of(1), false));
        config.setMode(ONLY_FIRST);
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(new SchedulerTask(ACTIVITY_LOG, NOW)));

        scheduler(config, new RuntimeException("boom")).start();

        verify(coordinator, never()).signalReady(any());
        verify(helper).handleFailure(any(), eq(ACTIVITY_LOG));
    }

    @Test
    void start_multiplePages_expandsUntilLastPage() {
        var config = config();
        config.setMode(ONLY_FIRST);
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(new SchedulerTask(ACTIVITY_LOG, NOW)));
        var scheduler = scheduler(config, step("d1", true, 10, "c1"), step("d2", false, 20, null));
        // update получает один и тот же мутируемый объект, поэтому фиксируем offset в момент вызова
        var updateOffsets = new ArrayList<Long>();
        var lastUpdated = new AtomicReference<SchedulerTask>();
        doAnswer(inv -> {
            var task = inv.<SchedulerTask>getArgument(0);
            updateOffsets.add(task.getOffset());
            lastUpdated.set(task);
            return Mono.just(task);
        }).when(repository).update(any());

        scheduler.start();

        assertEquals(List.of(0L, 10L), scheduler.getOffsets());
        assertEquals(java.util.Arrays.asList(null, "c1"), scheduler.getCursors());
        assertEquals(List.of(10L, 20L), updateOffsets);
        assertEquals(FINISHED, lastUpdated.get().getStatus());
        assertNull(lastUpdated.get().getCursor());
        verify(coordinator).signalReady(ACTIVITY_LOG);
    }

    @Test
    void start_forceMode_clearsFinishedTaskBeforeExecution() {
        var config = config();
        config.setMode(FORCE_FIRST);
        var finished = new SchedulerTask(ACTIVITY_LOG, NOW);
        finished.setOffset(999);
        finished.setCursor("old");
        finished.finish(NOW.minusDays(1));
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(finished));
        var scheduler = scheduler(config, step("d1", false, 5, "c1"));

        scheduler.start();

        assertEquals(List.of(0L), scheduler.getOffsets());
        assertEquals(1, scheduler.getCursors().size());
        assertNull(scheduler.getCursors().get(0));
    }

    @Test
    void start_normalMode_finishedTask_replacedByNewOne() {
        var config = config();
        config.setMode(NORMAL);
        var finished = new SchedulerTask(ACTIVITY_LOG, NOW);
        finished.finish(NOW.minusDays(1));
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(finished));
        var scheduler = scheduler(config, step("d1", false, 5, "c1"));

        scheduler.start();

        var captor = ArgumentCaptor.forClass(SchedulerTask.class);
        verify(repository).add(captor.capture());
        assertEquals(ACTIVITY_LOG, captor.getValue().getSchedulerType());
        assertEquals(NOW, captor.getValue().getStartAt());
        assertEquals(List.of(0L), scheduler.getOffsets());
    }

    @Test
    void start_saveStateDisabled_usesEmptyTaskAndSkipsRepository() {
        var config = config();
        config.setMode(ONLY_FIRST);
        var scheduler = scheduler(config, false, step("d1", false, 10, null));

        scheduler.start();

        verifyNoInteractions(repository);
        verify(coordinator).signalReady(ACTIVITY_LOG);
        assertEquals(List.of(0L), scheduler.getOffsets());
    }

    @Test
    void taskForCron_finishedTask_cleared() {
        var finished = new SchedulerTask(ACTIVITY_LOG, NOW);
        finished.setOffset(999);
        finished.setCursor("old");
        finished.finish(NOW.minusDays(1));
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(finished));
        var scheduler = scheduler(config());

        var result = scheduler.taskForCron().block();

        assertEquals(0L, result.getOffset());
        assertNull(result.getCursor());
        assertEquals(IN_PROGRESS, result.getStatus());
    }

    @Test
    void taskForCron_unfinishedTask_untouched() {
        var task = new SchedulerTask(ACTIVITY_LOG, NOW);
        task.setOffset(7);
        task.setCursor("c");
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(task));
        var scheduler = scheduler(config());

        var result = scheduler.taskForCron().block();

        assertEquals(7L, result.getOffset());
        assertEquals("c", result.getCursor());
        assertEquals(IN_PROGRESS, result.getStatus());
    }

    @Test
    void taskForCron_noTask_addsNew() {
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.empty());
        var scheduler = scheduler(config());

        var result = scheduler.taskForCron().block();

        assertEquals(ACTIVITY_LOG, result.getSchedulerType());
        assertEquals(NOW, result.getStartAt());
    }

    @Test
    void taskForCron_saveStateDisabled_returnsEmpty() {
        var scheduler = scheduler(config(), false);

        var result = scheduler.taskForCron().block();

        assertSame(SchedulerTask.EMPTY, result);
        verifyNoInteractions(repository);
    }

    private TestConfig config() {
        var config = new TestConfig();
        config.setCronExpression("* * * * * *");
        config.setFirstStart(new SchedulerPropertiesConfig.FirstStart(List.of(1), true));
        return config;
    }

    private TestScheduler scheduler(TestConfig config) {
        return new TestScheduler(config, helper, repository, date, transactional, coordinator,
                List.of(), null, true, null, metrics);
    }

    private TestScheduler scheduler(TestConfig config, Set<SchedulerType> dependencies) {
        return new TestScheduler(config, helper, repository, date, transactional, coordinator,
                List.of(), dependencies, true, null, metrics);
    }

    private TestScheduler scheduler(TestConfig config, Scheduler.Step<String>... steps) {
        return new TestScheduler(config, helper, repository, date, transactional, coordinator,
                List.of(steps), null, true, null, metrics);
    }

    private TestScheduler scheduler(TestConfig config, boolean saveState, Scheduler.Step<String>... steps) {
        return new TestScheduler(config, helper, repository, date, transactional, coordinator,
                List.of(steps), null, saveState, null, metrics);
    }

    private TestScheduler scheduler(TestConfig config, RuntimeException failure, Scheduler.Step<String>... steps) {
        return new TestScheduler(config, helper, repository, date, transactional, coordinator,
                List.of(steps), null, true, failure, metrics);
    }

    private static Scheduler.Step<String> step(String data, boolean hasNext, long nextOffset, String nextCursor) {
        return new Scheduler.Step<>(data, hasNext, nextOffset, nextCursor);
    }

    private static class TestConfig extends SchedulerPropertiesConfig {
    }

    private static class TestScheduler extends Scheduler<String, TestConfig> {
        private final List<Scheduler.Step<String>> steps;
        private final Set<SchedulerType> dependencies;
        private final boolean saveState;
        private final RuntimeException failure;
        private final AtomicInteger stepCalls = new AtomicInteger();
        private final List<Long> offsets = new ArrayList<>();
        private final List<String> cursors = new ArrayList<>();

        TestScheduler(TestConfig config, SchedulerHelper helper, SchedulerTaskRepository repository,
                      DateUtils date, TransactionalOperator transactional, SchedulerCoordinator coordinator,
                      List<Scheduler.Step<String>> steps, Set<SchedulerType> dependencies, boolean saveState,
                      RuntimeException failure, SyncTaskMetrics syncTaskMetrics) {
            super(ACTIVITY_LOG, config, helper, repository, date, transactional, coordinator, syncTaskMetrics);
            this.steps = steps;
            this.dependencies = dependencies;
            this.saveState = saveState;
            this.failure = failure;
        }

        @Override
        protected Mono<Scheduler.Step<String>> executeStep(Long offset, String cursor) {
            offsets.add(offset);
            cursors.add(cursor);
            var call = stepCalls.getAndIncrement();
            if (failure != null && call == 0) {
                return Mono.error(failure);
            }
            var index = failure != null ? call - 1 : call;
            return index < steps.size()
                    ? Mono.just(steps.get(index))
                    : Mono.error(new IllegalStateException("no more steps"));
        }

        @Override
        protected Mono<Boolean> afterStep(String data) {
            return Mono.just(true);
        }

        @Override
        protected Set<SchedulerType> getDependencies() {
            return dependencies;
        }

        @Override
        protected boolean saveStateTask() {
            return saveState;
        }

        Mono<SchedulerTask> taskForCron() {
            return super.getTaskForProcessByCron();
        }

        List<Long> getOffsets() {
            return offsets;
        }

        List<String> getCursors() {
            return cursors;
        }
    }
}
