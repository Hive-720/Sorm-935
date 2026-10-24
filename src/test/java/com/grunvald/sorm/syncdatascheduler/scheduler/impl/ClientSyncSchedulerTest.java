package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.client.ClientSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.client.ClientSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.FINISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientSyncSchedulerTest extends SyncSchedulerTestBase {

    private ClientSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(ClientSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service);
    }

    private ClientSchedulerPropertiesConfig config() {
        var config = new ClientSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        config.setLimit(25);
        return config;
    }

    @Test
    void executeStep_mapsPageToStep_withNullNextCursor() {
        var page = ClientSyncPage.builder()
                .elements(List.of())
                .hasNextPage(true)
                .nextOffset(25L)
                .build();
        when(service.getActual(25L, 25)).thenReturn(Mono.just(page));

        var step = scheduler.execute(25L, "ignored");

        assertSame(page, step.data());
        assertTrue(step.hasNextPage());
        assertEquals(25L, step.nextOffset());
        assertNull(step.nextCursor());
        verify(service).getActual(25L, 25);
    }

    @Test
    void afterStep_appliesActualData() {
        var page = ClientSyncPage.builder().elements(List.of()).build();
        when(service.applyActualData(page)).thenReturn(Mono.just(true));

        StepVerifier.create(scheduler.afterStep(page))
                .expectNext(true)
                .verifyComplete();

        verify(service).applyActualData(page);
    }

    @Test
    void executeStep_propagatesErrorFromSyncService() {
        when(service.getActual(0L, 25))
                .thenReturn(Mono.error(new IllegalStateException("boom")));

        assertThrows(IllegalStateException.class, () -> scheduler.executeStep(0L, null).block());
    }

    @Test
    void start_onlyFirstMode_runsStepAppliesDataAndFinishesTask() {
        when(repository.findBySchedulerType(CLIENT)).thenReturn(Mono.just(new SchedulerTask(CLIENT, NOW)));
        var page = ClientSyncPage.builder()
                .elements(List.of())
                .hasNextPage(false)
                .nextOffset(25L)
                .build();
        when(service.getActual(0L, 25)).thenReturn(Mono.just(page));
        when(service.applyActualData(page)).thenReturn(Mono.just(true));

        scheduler.start();

        verify(service).applyActualData(page);
        verify(coordinator).signalReady(CLIENT);
        var captor = ArgumentCaptor.forClass(SchedulerTask.class);
        verify(repository).update(captor.capture());
        var updated = captor.getValue();
        assertEquals(25L, updated.getOffset());
        assertNull(updated.getCursor());
        assertEquals(FINISHED, updated.getStatus());
        assertEquals(NOW, updated.getFinishAt());
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends ClientSyncScheduler {

        TestableScheduler(ClientSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, ClientSyncService service) {
            super(config, helper, repository, date, transactional, coordinator, service);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
