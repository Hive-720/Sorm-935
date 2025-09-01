package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.order.OrderSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.order.OrderSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.OrderSchedulerPropertiesConfig;
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
@ConditionalOnBean(OrderSchedulerPropertiesConfig.class)
public class OrderSyncScheduler extends Scheduler<OrderSyncPage, OrderSchedulerPropertiesConfig> {
    private final OrderSyncService orderSyncService;


    public OrderSyncScheduler(OrderSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                              SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                              TransactionalOperator transactional, SchedulerCoordinator coordinator,
                              OrderSyncService orderSyncService) {
        super(config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
        this.orderSyncService = orderSyncService;
    }

    @Override
    protected Mono<Step<OrderSyncPage>> executeStep(Long offset, String cursor) {
        var historyDepthDate = date.now().minusYears(config.getMaxAgeYears());
        return orderSyncService.getActualData(offset, config.getLimit(), historyDepthDate)
                .map(page -> new Step<>(
                                page,
                                page.isHasNextPage(),
                                page.getNextOffset(),
                                null
                        )
                );
    }

    @Override
    protected Mono<Boolean> afterStep(OrderSyncPage page) {
        return orderSyncService.applyActualData(page);
    }
}