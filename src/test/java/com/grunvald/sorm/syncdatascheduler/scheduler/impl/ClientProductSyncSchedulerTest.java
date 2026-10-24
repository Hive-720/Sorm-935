package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.clientproduct.ClientProductSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.clientproduct.ClientProductSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientProductSchedulerPropertiesConfig;
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

class ClientProductSyncSchedulerTest extends SyncSchedulerTestBase {

    private ClientProductSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(ClientProductSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service);
    }

    private ClientProductSchedulerPropertiesConfig config() {
        var config = new ClientProductSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        config.setLimit(25);
        return config;
    }

    @Test
    void executeStep_mapsPageToStep() {
        var page = ClientProductSyncPage.builder()
                .elements(List.of())
                .hasNextPage(true)
                .nextOffset(30L)
                .build();
        when(service.getActualData(25L, 25)).thenReturn(Mono.just(page));

        var step = scheduler.execute(25L, null);

        assertSame(page, step.data());
        assertTrue(step.hasNextPage());
        assertEquals(30L, step.nextOffset());
        verify(service).getActualData(25L, 25);
    }

    @Test
    void afterStep_appliesActualData() {
        var page = ClientProductSyncPage.builder().elements(List.of()).build();
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

    private static class TestableScheduler extends ClientProductSyncScheduler {

        TestableScheduler(ClientProductSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, ClientProductSyncService service) {
            super(config, helper, repository, date, transactional, coordinator, service);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
