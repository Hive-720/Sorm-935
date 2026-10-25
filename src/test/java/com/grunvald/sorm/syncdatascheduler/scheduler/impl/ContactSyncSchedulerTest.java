package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.contact.ContactSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.contact.ContactSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ContactSchedulerPropertiesConfig;
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

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContactSyncSchedulerTest extends SyncSchedulerTestBase {

    private ContactSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(ContactSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service, metrics);
    }

    private ContactSchedulerPropertiesConfig config() {
        var config = new ContactSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        config.setLimit(25);
        return config;
    }

    @Test
    void executeStep_mapsPageToStep() {
        var page = ContactSyncPage.builder()
                .elements(List.of())
                .hasNextPage(true)
                .nextOffset(50L)
                .build();
        when(service.getActualData(25L, 25)).thenReturn(Mono.just(page));

        var step = scheduler.execute(25L, "c1");

        assertSame(page, step.data());
        assertTrue(step.hasNextPage());
        assertEquals(50L, step.nextOffset());
        assertEquals("c1", step.nextCursor());
        verify(service).getActualData(25L, 25);
    }

    @Test
    void afterStep_appliesActualData() {
        var page = ContactSyncPage.builder().elements(List.of()).build();
        when(service.applyActualData(page)).thenReturn(Mono.just(true));

        StepVerifier.create(scheduler.afterStep(page))
                .expectNext(true)
                .verifyComplete();

        verify(service).applyActualData(page);
    }

    @Test
    void executeStep_propagatesErrorFromSyncService() {
        when(service.getActualData(0L, 25))
                .thenReturn(Mono.error(new IllegalStateException("boom")));

        assertThrows(IllegalStateException.class, () -> scheduler.executeStep(0L, null).block());
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends ContactSyncScheduler {

        TestableScheduler(ContactSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, ContactSyncService service, SyncTaskMetrics syncTaskMetrics) {
            super(config, helper, repository, date, transactional, coordinator, service, syncTaskMetrics);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
