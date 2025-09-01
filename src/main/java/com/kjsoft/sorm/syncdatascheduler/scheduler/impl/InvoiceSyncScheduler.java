package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.invoice.InvoiceSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.invoice.InvoiceSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.InvoiceSchedulerPropertiesConfig;
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
@ConditionalOnBean(InvoiceSchedulerPropertiesConfig.class)
public class InvoiceSyncScheduler extends Scheduler<InvoiceSyncPage, InvoiceSchedulerPropertiesConfig> {
    private final InvoiceSyncService invoiceSyncService;


    public InvoiceSyncScheduler(InvoiceSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                InvoiceSyncService invoiceSyncService) {
        super(config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
        this.invoiceSyncService = invoiceSyncService;
    }


    @Override
    protected Mono<Step<InvoiceSyncPage>> executeStep(Long offset, String cursor) {
        var historyDepthDate = date.now().minusYears(config.getMaxAgeYears());
        return invoiceSyncService.getActualData(offset, config.getLimit(), historyDepthDate)
                .map(page -> new Step<>(page, page.isHasNextPage(), page.getNextOffset(), cursor));
    }

    @Override
    protected Mono<Boolean> afterStep(InvoiceSyncPage page) {
        return invoiceSyncService.applyActualData(page);
    }
}