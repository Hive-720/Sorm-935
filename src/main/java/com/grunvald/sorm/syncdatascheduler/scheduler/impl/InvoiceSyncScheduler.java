package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.invoice.InvoiceSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.invoice.InvoiceSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.InvoiceSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.INVOICE;


@Component
@ConditionalOnBean(InvoiceSchedulerPropertiesConfig.class)
public class InvoiceSyncScheduler extends Scheduler<InvoiceSyncPage, InvoiceSchedulerPropertiesConfig> {
    private final InvoiceSyncService invoiceSyncService;


    public InvoiceSyncScheduler(InvoiceSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                InvoiceSyncService invoiceSyncService) {
        super(INVOICE, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
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