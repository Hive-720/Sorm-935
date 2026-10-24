package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.contact.ContactSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ContactDeleteSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ContactDeleteSyncSchedulerTest extends SyncSchedulerTestBase {

    private ContactSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(ContactSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service);
    }

    private ContactDeleteSchedulerPropertiesConfig config() {
        var config = new ContactDeleteSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setLimit(25);
        return config;
    }

    @Test
    void executeStep_mapsDeleteResultToFinishedStep() {
        when(service.delete(25L, 25)).thenReturn(Mono.just(true));

        var step = scheduler.execute(25L, "c1");

        assertEquals(Boolean.TRUE, step.data());
        assertEquals(false, step.hasNextPage());
        assertNull(step.nextOffset());
        assertNull(step.nextCursor());
        verify(service).delete(25L, 25);
    }

    @Test
    void afterStep_alwaysReturnsTrue() {
        StepVerifier.create(scheduler.afterStep(false))
                .expectNext(true)
                .verifyComplete();

        verifyNoInteractions(service);
    }

    @Test
    void saveStateTask_isDisabled() {
        assertFalse(scheduler.saveStateTask());
    }

    @Test
    void executeStep_propagatesErrorFromSyncService() {
        when(service.delete(0L, 25))
                .thenReturn(Mono.error(new IllegalStateException("boom")));

        assertThrows(IllegalStateException.class, () -> scheduler.executeStep(0L, null).block());
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends ContactDeleteSyncScheduler {

        TestableScheduler(ContactDeleteSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, ContactSyncService service) {
            super(config, helper, repository, date, transactional, coordinator, service);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
