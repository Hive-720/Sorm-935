package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.user.UserSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.user.UserSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.UserSchedulerPropertiesConfig;
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
import java.util.Set;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserSyncSchedulerTest extends SyncSchedulerTestBase {

    private UserSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(UserSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service);
    }

    private UserSchedulerPropertiesConfig config() {
        var config = new UserSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        config.setLimit(25);
        return config;
    }

    @Test
    void executeStep_mapsPageToStep() {
        var page = UserSyncPage.builder()
                .users(List.of())
                .relations(List.of())
                .hasNextPage(true)
                .nextOffset(70L)
                .build();
        when(service.getActualData(25L, 25)).thenReturn(Mono.just(page));

        var step = scheduler.execute(25L, "c1");

        assertSame(page, step.data());
        assertTrue(step.hasNextPage());
        assertEquals(70L, step.nextOffset());
        assertEquals("c1", step.nextCursor());
        verify(service).getActualData(25L, 25);
    }

    @Test
    void afterStep_appliesActualData() {
        var page = UserSyncPage.builder().users(List.of()).relations(List.of()).build();
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
        when(service.getActualData(0L, 25))
                .thenReturn(Mono.error(new IllegalStateException("boom")));

        assertThrows(IllegalStateException.class, () -> scheduler.executeStep(0L, null).block());
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends UserSyncScheduler {

        TestableScheduler(UserSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, UserSyncService service) {
            super(config, helper, repository, date, transactional, coordinator, service);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
