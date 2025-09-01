package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.product.ProductSync;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.product.ProductSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.ProductSchedulerPropertiesConfig;
import com.kjsoft.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.kjsoft.sorm.syncdatascheduler.scheduler.Scheduler;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.kjsoft.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;


@Component
@ConditionalOnBean(ProductSchedulerPropertiesConfig.class)
public class ProductSyncScheduler extends Scheduler<ProductSync, ProductSchedulerPropertiesConfig> {
    private final ProductSyncService productSyncService;


    public ProductSyncScheduler(ProductSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                ProductSyncService productSyncService) {
        super(config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
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