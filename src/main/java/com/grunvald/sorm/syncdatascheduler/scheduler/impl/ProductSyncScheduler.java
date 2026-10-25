package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.product.ProductSync;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.product.ProductSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ProductSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SyncTaskMetrics;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.PRODUCT;


@Component
@ConditionalOnBean(ProductSchedulerPropertiesConfig.class)
public class ProductSyncScheduler extends Scheduler<ProductSync, ProductSchedulerPropertiesConfig> {
    private final ProductSyncService productSyncService;


    public ProductSyncScheduler(ProductSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                ProductSyncService productSyncService, SyncTaskMetrics syncTaskMetrics) {
        super(PRODUCT, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator, syncTaskMetrics);
        this.productSyncService = productSyncService;
    }

    @Override
    protected Mono<Step<ProductSync>> executeStep(Long offset, String cursor) {
        return productSyncService.getActualProducts()
                .map(data -> new Step<>(data, false, null, null));
    }

    @Override
    protected Mono<Boolean> afterStep(ProductSync data) {
        return productSyncService.applyActualData(data);
    }

    @Override
    protected boolean saveStateTask() {
        return false;
    }
}