package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.order.OrderSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.order.OrderSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.OrderSchedulerPropertiesConfig;
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

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.ORDER;


@Component
@ConditionalOnBean(OrderSchedulerPropertiesConfig.class)
public class OrderSyncScheduler extends Scheduler<OrderSyncPage, OrderSchedulerPropertiesConfig> {
    private final OrderSyncService orderSyncService;


    public OrderSyncScheduler(OrderSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                              SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                              TransactionalOperator transactional, SchedulerCoordinator coordinator,
                              OrderSyncService orderSyncService, SyncTaskMetrics syncTaskMetrics) {
        super(ORDER, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator, syncTaskMetrics);
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