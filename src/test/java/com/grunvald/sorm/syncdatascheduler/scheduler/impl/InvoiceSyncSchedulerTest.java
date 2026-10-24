package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.invoice.InvoiceSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.invoice.InvoiceSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.InvoiceSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InvoiceSyncSchedulerTest extends SyncSchedulerTestBase {

    private InvoiceSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(InvoiceSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service);
    }

    private InvoiceSchedulerPropertiesConfig config() {
        var config = new InvoiceSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        config.setLimit(25);
        config.setMaxAgeYears(3);
        return config;
    }

    @Test
    void executeStep_mapsPageToStep_withHistoryDepthDate() {
        var page = InvoiceSyncPage.builder()
                .invoices(List.of())
                .transactions(List.of())
                .hasNextPage(true)
                .nextOffset(40L)
                .build();
        when(service.getActualData(25L, 25, NOW.minusYears(3))).thenReturn(Mono.just(page));

        var step = scheduler.execute(25L, "c1");

        assertSame(page, step.data());
        assertTrue(step.hasNextPage());
        assertEquals(40L, step.nextOffset());
        assertEquals("c1", step.nextCursor());
        verify(service).getActualData(25L, 25, NOW.minusYears(3));
    }

    @Test
    void afterStep_appliesActualData() {
        var page = InvoiceSyncPage.builder().invoices(List.of()).transactions(List.of()).build();
        when(service.applyActualData(page)).thenReturn(Mono.just(true));

        StepVerifier.create(scheduler.afterStep(page))
                .expectNext(true)
                .verifyComplete();

        verify(service).applyActualData(page);
    }

    @Test
    void executeStep_propagatesErrorFromSyncService() {
        when(service.getActualData(0L, 25, NOW.minusYears(3)))
                .thenReturn(Mono.error(new IllegalStateException("boom")));

        assertThrows(IllegalStateException.class, () -> scheduler.executeStep(0L, null).block());
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends InvoiceSyncScheduler {

        TestableScheduler(InvoiceSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, InvoiceSyncService service) {
            super(config, helper, repository, date, transactional, coordinator, service);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
