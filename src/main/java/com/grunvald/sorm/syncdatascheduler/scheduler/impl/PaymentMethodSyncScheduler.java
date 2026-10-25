package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.paymentmethod.PaymentMethodSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.PaymentMethodSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SyncTaskMetrics;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.PAYMENT_METHOD;


@Component
@ConditionalOnBean(PaymentMethodSchedulerPropertiesConfig.class)
public class PaymentMethodSyncScheduler extends Scheduler<List<PaymentMethod>, PaymentMethodSchedulerPropertiesConfig> {
    private final PaymentMethodSyncService paymentMethodService;


    public PaymentMethodSyncScheduler(PaymentMethodSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                      SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                      TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                      PaymentMethodSyncService paymentMethodService, SyncTaskMetrics syncTaskMetrics) {
        super(PAYMENT_METHOD, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator, syncTaskMetrics);
        this.paymentMethodService = paymentMethodService;
    }

    @Override
    protected Mono<Step<List<PaymentMethod>>> executeStep(Long offset, String cursor) {
        return paymentMethodService.getActualData()
                .map(list -> new Step<>(list, false, offset, cursor));
    }

    @Override
    protected Mono<Boolean> afterStep(List<PaymentMethod> data) {
        return paymentMethodService.applyActualData(data);
    }

    @Override
    protected boolean saveStateTask() {
        return false;
    }
}