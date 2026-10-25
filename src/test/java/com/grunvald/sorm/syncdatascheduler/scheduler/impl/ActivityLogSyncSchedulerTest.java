package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.activitylog.ActivityLogSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.activitylog.ActivityLogSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ActivityLogSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SyncTaskMetrics;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.ACTIVITY_LOG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActivityLogSyncSchedulerTest extends SyncSchedulerTestBase {

    private ActivityLogSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(ActivityLogSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service, metrics);
    }

    private ActivityLogSchedulerPropertiesConfig config() {
        var config = new ActivityLogSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        config.setLimit(100);
        config.setMaxAgeYears(3);
        config.setForbiddenDescriptionPatterns(List.of("spam", "test"));
        return config;
    }

    @Test
    void executeStep_nullCursor_usesMinDateAndMapsPage() {
        var page = ActivityLogSyncPage.builder()
                .elements(List.of())
                .hasNextPage(true)
                .nextOffset(130L)
                .build();
        when(service.getActualData(10L, 100, NOW.minusYears(3), List.of("spam", "test")))
                .thenReturn(Mono.just(page));

        var step = scheduler.execute(10L, null);

        assertSame(page, step.data());
        assertTrue(step.hasNextPage());
        assertEquals(130L, step.nextOffset());
        assertNull(step.nextCursor());
    }

    @Test
    void executeStep_cursorNewerThanMinDate_usesCursorDate() {
        var cursor = "2025-06-01T00:00:00Z";
        var page = ActivityLogSyncPage.builder()
                .elements(List.of())
                .hasNextPage(false)
                .nextOffset(null)
                .build();
        when(service.getActualData(0L, 100, OffsetDateTime.parse(cursor), List.of("spam", "test")))
                .thenReturn(Mono.just(page));

        var step = scheduler.execute(0L, cursor);

        assertSame(page, step.data());
        assertEquals(cursor, step.nextCursor());
    }

    @Test
    void executeStep_cursorOlderThanMinDate_usesMinDate() {
        var cursor = "2020-01-01T00:00:00Z";
        var page = ActivityLogSyncPage.builder()
                .elements(List.of())
                .hasNextPage(false)
                .nextOffset(null)
                .build();
        when(service.getActualData(0L, 100, NOW.minusYears(3), List.of("spam", "test")))
                .thenReturn(Mono.just(page));

        scheduler.execute(0L, cursor);

        verify(service).getActualData(0L, 100, NOW.minusYears(3), List.of("spam", "test"));
    }

    @Test
    void afterStep_appliesActualData() {
        var page = ActivityLogSyncPage.builder().elements(List.of()).build();
        when(service.applyActualData(page)).thenReturn(Mono.just(true));

        StepVerifier.create(scheduler.afterStep(page))
                .expectNext(true)
                .verifyComplete();

        verify(service).applyActualData(page);
    }

    @Test
    void taskForCron_nullCursor_fetchesLatestCursor() {
        var task = new SchedulerTask(ACTIVITY_LOG, NOW);
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(task));
        when(service.getLatestCursor()).thenReturn(Mono.just(NOW));

        var result = scheduler.cronTask();

        assertEquals(NOW.toString(), result.getCursor());
        verify(service).getLatestCursor();
    }

    @Test
    void taskForCron_nonNullCursor_skipsLatestCursor() {
        var task = new SchedulerTask(ACTIVITY_LOG, NOW);
        task.setCursor("2025-01-01T00:00:00Z");
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(task));

        var result = scheduler.cronTask();

        assertEquals("2025-01-01T00:00:00Z", result.getCursor());
        verify(service, never()).getLatestCursor();
    }

    @Test
    void executeStep_propagatesErrorFromSyncService() {
        when(service.getActualData(anyLong(), anyInt(), any(), any()))
                .thenReturn(Mono.error(new IllegalStateException("boom")));

        assertThrows(IllegalStateException.class, () -> scheduler.executeStep(0L, null).block());
    }

    @Test
    void start_syncServiceError_handledAndSignalsReady() {
        when(repository.findBySchedulerType(ACTIVITY_LOG)).thenReturn(Mono.just(new SchedulerTask(ACTIVITY_LOG, NOW)));
        when(service.getActualData(anyLong(), anyInt(), any(), any()))
                .thenReturn(Mono.error(new IllegalStateException("boom")));

        scheduler.start();

        verify(helper, atLeastOnce()).handleError(any(), eq(ACTIVITY_LOG));
        verify(coordinator).signalReady(ACTIVITY_LOG);
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends ActivityLogSyncScheduler {

        TestableScheduler(ActivityLogSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, ActivityLogSyncService service, SyncTaskMetrics syncTaskMetrics) {
            super(config, helper, repository, date, transactional, coordinator, service, syncTaskMetrics);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }

        SchedulerTask cronTask() {
            return getTaskForProcessByCron().block();
        }
    }
}
