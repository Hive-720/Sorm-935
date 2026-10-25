package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.client.ClientDetailsSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.client.ClientSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientDetailSchedulerPropertiesConfig;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientDetailSyncSchedulerTest extends SyncSchedulerTestBase {

    private ClientSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(ClientSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service, metrics);
    }

    private ClientDetailSchedulerPropertiesConfig config() {
        var config = new ClientDetailSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        config.setClientLimit(25);
        config.setConcurrency(4);
        return config;
    }

    @Test
    void executeStep_withCursor_parsesCursorAndMapsPage() {
        var page = page(true, 42L);
        when(service.getActualClientDetails(7L, 25, 4)).thenReturn(Mono.just(page));

        var step = scheduler.execute(0L, "7");

        assertSame(page, step.data());
        assertTrue(step.hasNextPage());
        assertNull(step.nextOffset());
        assertEquals("42", step.nextCursor());
        verify(service).getActualClientDetails(7L, 25, 4);
    }

    @Test
    void executeStep_nullCursor_nullPageCursor_nextCursorIsNull() {
        var page = page(false, null);
        when(service.getActualClientDetails(null, 25, 4)).thenReturn(Mono.just(page));

        var step = scheduler.execute(0L, null);

        assertSame(page, step.data());
        assertNull(step.nextCursor());
        verify(service).getActualClientDetails(null, 25, 4);
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

    private ClientDetailsSyncPage page(boolean hasNextPage, Long cursor) {
        return ClientDetailsSyncPage.builder()
                .actual(List.of())
                .lastLogins(List.of())
                .removedClients(Set.of())
                .hasNextPage(hasNextPage)
                .cursor(cursor)
                .build();
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends ClientDetailSyncScheduler {

        TestableScheduler(ClientDetailSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, ClientSyncService service, SyncTaskMetrics syncTaskMetrics) {
            super(config, helper, repository, date, transactional, coordinator, service, syncTaskMetrics);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
