package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.clientgroup.ClientGroupSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientGroupSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientGroupSyncSchedulerTest extends SyncSchedulerTestBase {

    private ClientGroupSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(ClientGroupSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service);
    }

    private ClientGroupSchedulerPropertiesConfig config() {
        var config = new ClientGroupSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        return config;
    }

    @Test
    void executeStep_mapsListWithNoNextPage() {
        var groups = List.of(new ClientGroup("default", new BigDecimal("10.50")));
        when(service.getActualData()).thenReturn(Mono.just(groups));

        var step = scheduler.execute(5L, "c1");

        assertSame(groups, step.data());
        assertFalse(step.hasNextPage());
        assertEquals(5L, step.nextOffset());
        assertEquals("c1", step.nextCursor());
        verify(service).getActualData();
    }

    @Test
    void afterStep_appliesActualData() {
        var groups = List.<ClientGroup>of();
        when(service.applyActualData(groups)).thenReturn(Mono.just(true));

        StepVerifier.create(scheduler.afterStep(groups))
                .expectNext(true)
                .verifyComplete();

        verify(service).applyActualData(groups);
    }

    @Test
    void saveStateTask_isDisabled() {
        assertFalse(scheduler.saveStateTask());
    }

    @Test
    void executeStep_propagatesErrorFromSyncService() {
        when(service.getActualData()).thenReturn(Mono.error(new IllegalStateException("boom")));

        assertThrows(IllegalStateException.class, () -> scheduler.executeStep(0L, null).block());
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends ClientGroupSyncScheduler {

        TestableScheduler(ClientGroupSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, ClientGroupSyncService service) {
            super(config, helper, repository, date, transactional, coordinator, service);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
