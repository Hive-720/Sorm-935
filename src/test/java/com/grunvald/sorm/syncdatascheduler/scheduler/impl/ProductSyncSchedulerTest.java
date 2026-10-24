package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.product.ProductSync;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.product.ProductSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ProductSchedulerPropertiesConfig;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductSyncSchedulerTest extends SyncSchedulerTestBase {

    private ProductSyncService service;
    private TestableScheduler scheduler;

    @BeforeEach
    void setUp() {
        service = mock(ProductSyncService.class);
        scheduler = new TestableScheduler(config(), helper, repository, date, transactional, coordinator, service);
    }

    private ProductSchedulerPropertiesConfig config() {
        var config = new ProductSchedulerPropertiesConfig();
        config.setCronExpression("* * * * * *");
        config.setMode(ONLY_FIRST);
        config.setFirstStart(firstStart());
        return config;
    }

    @Test
    void executeStep_mapsDataToFinishedStep() {
        var data = ProductSync.builder()
                .products(List.of())
                .productConfigs(List.of())
                .productConfigRelations(List.of())
                .productConfigOptions(List.of())
                .build();
        when(service.getActualProducts()).thenReturn(Mono.just(data));

        var step = scheduler.execute(5L, "c1");

        assertSame(data, step.data());
        assertEquals(false, step.hasNextPage());
        assertNull(step.nextOffset());
        assertNull(step.nextCursor());
        verify(service).getActualProducts();
    }

    @Test
    void afterStep_appliesActualData() {
        var data = ProductSync.builder().build();
        when(service.applyActualData(data)).thenReturn(Mono.just(true));

        StepVerifier.create(scheduler.afterStep(data))
                .expectNext(true)
                .verifyComplete();

        verify(service).applyActualData(data);
    }

    @Test
    void saveStateTask_isDisabled() {
        assertFalse(scheduler.saveStateTask());
    }

    @Test
    void executeStep_propagatesErrorFromSyncService() {
        when(service.getActualProducts()).thenReturn(Mono.error(new IllegalStateException("boom")));

        assertThrows(IllegalStateException.class, () -> scheduler.executeStep(0L, null).block());
    }

    private record StepView(Object data, boolean hasNextPage, Long nextOffset, String nextCursor) {
    }

    private static class TestableScheduler extends ProductSyncScheduler {

        TestableScheduler(ProductSchedulerPropertiesConfig config, SchedulerHelper helper,
                          SchedulerTaskRepository repository, DateUtils date, TransactionalOperator transactional,
                          SchedulerCoordinator coordinator, ProductSyncService service) {
            super(config, helper, repository, date, transactional, coordinator, service);
        }

        StepView execute(Long offset, String cursor) {
            var step = executeStep(offset, cursor).block();
            return new StepView(step.getData(), step.isHasNextPage(), step.getNextOffset(), step.getNextCursor());
        }
    }
}
