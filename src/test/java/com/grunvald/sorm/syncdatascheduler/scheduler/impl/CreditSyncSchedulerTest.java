package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.credit.CreditSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.credit.CreditSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.CreditSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SyncTaskMetrics;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreditSyncSchedulerTest extends SyncSchedulerTestBase {

    private CreditSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(CreditSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service, metrics);
    }

    private CreditSchedulerPropertiesConfig config() {
        var config = new CreditSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        config.setClientLimit(25);
        config.setMaxAgeYears(3);
        config.setMaxClientCreditPerTask(1000);
        return config;
    }

    @Test
    void executeStep_nullCursor_usesNullCursorAndHistoryDepthDate() {
        var page = page(true, 99L);
        when(service.getActualData(null, 25, 1000, NOW.minusYears(3))).thenReturn(Mono.just(page));

        var step = scheduler.execute(0L, null);

        assertSame(page, step.data());
        assertTrue(step.hasNextPage());
        assertNull(step.nextOffset());
        assertEquals("99", step.nextCursor());
        verify(service).getActualData(null, 25, 1000, NOW.minusYears(3));
    }

    @Test
    void executeStep_withCursor_parsesCursor() {
        var page = page(false, null);
        when(service.getActualData(42L, 25, 1000, NOW.minusYears(3))).thenReturn(Mono.just(page));

        var step = scheduler.execute(0L, "42");

        assertSame(page, step.data());
        assertNull(step.nextCursor());
        verify(service).getActualData(42L, 25, 1000, NOW.minusYears(3));
    }

    @Test
    void afterStep_appliesActualData() {
        var page = page(false, null);
        when(service.applyActualData(page)).thenReturn(Mono.just(true));

        StepVerifier.create(scheduler.afterStep(page))
                .expectNext(true)
                .verifyComplete();

        verify(service).applyActualData(page);
    }

    @Test
    void getDependencies_returnsClient() {
        assertEquals(Set.of(CLIENT), scheduler.getDependencies());
    }

    @Test
    void executeStep_propagatesErrorFromSyncService() {
        when(service.getActualData(null, 25, 1000, NOW.minusYears(3)))
                .thenReturn(Mono.error(new IllegalStateException("boom")));

        assertThrows(IllegalStateException.class, () -> scheduler.executeStep(0L, null).block());
    }

    private CreditSyncPage page(boolean hasNextPage, Long cursor) {
        return CreditSyncPage.builder()
                .credits(List.of())
                .hasNextPage(hasNextPage)
                .cursor(cursor)
                .build();
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends CreditSyncScheduler {

        TestableScheduler(CreditSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, CreditSyncService service, SyncTaskMetrics syncTaskMetrics) {
            super(config, helper, repository, date, transactional, coordinator, service, syncTaskMetrics);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
