package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.contact.ContactSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.contact.ContactSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.ContactSchedulerPropertiesConfig;
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
@ConditionalOnBean(ContactSchedulerPropertiesConfig.class)
public class ContactSyncScheduler extends Scheduler<ContactSyncPage, ContactSchedulerPropertiesConfig> {
    private final ContactSyncService contactSyncService;


    public ContactSyncScheduler(ContactSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                ContactSyncService contactSyncService) {
        super(config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
        this.contactSyncService = contactSyncService;
    }

    @Override
    protected Mono<Step<ContactSyncPage>> executeStep(Long offset, String cursor) {
        return contactSyncService.getActualData(offset, config.getLimit())
                .map(page -> new Step<>(page, page.isHasNextPage(), page.getNextOffset(), cursor));
    }

    @Override
    protected Mono<Boolean> afterStep(ContactSyncPage page) {
        return contactSyncService.applyActualData(page);
    }
}