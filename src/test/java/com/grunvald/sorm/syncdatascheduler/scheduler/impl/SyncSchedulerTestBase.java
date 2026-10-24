package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.reactivestreams.Publisher;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Общая настройка моков для тестов конкретных планировщиков.
 * Конвенции повторяют {@code SchedulerTest}: plain-mock'и, null-безопасные
 * заглушки add/update (ветка switchIfEmpty вычисляется жадно при сборке цепочки),
 * Retry.max(1), чтобы retry-задержки не зависали, и transactional-заглушка-прокси.
 */
abstract class SyncSchedulerTestBase {

    protected static final OffsetDateTime NOW = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    protected SchedulerHelper helper;
    protected SchedulerTaskRepository repository;
    protected DateUtils date;
    protected TransactionalOperator transactional;
    protected SchedulerCoordinator coordinator;

    @BeforeEach
    void setUpBase() {
        helper = mock(SchedulerHelper.class);
        repository = mock(SchedulerTaskRepository.class);
        date = mock(DateUtils.class);
        transactional = mock(TransactionalOperator.class);
        coordinator = mock(SchedulerCoordinator.class);

        when(helper.createRetry(any(), any())).thenReturn(Retry.max(1));
        when(date.now()).thenReturn(NOW);
        when(transactional.transactional(any(Mono.class)))
                .thenAnswer(inv -> Mono.from(inv.getArgument(0, Publisher.class)));
        when(coordinator.waitUploadFor(any())).thenReturn(Mono.empty());
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

    protected SchedulerPropertiesConfig.FirstStart firstStart() {
        return new SchedulerPropertiesConfig.FirstStart(List.of(1), true);
    }
}
