package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.contact.ContactSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.contact.ContactSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ContactSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CONTACT;


@Component
@ConditionalOnBean(ContactSchedulerPropertiesConfig.class)
public class ContactSyncScheduler extends Scheduler<ContactSyncPage, ContactSchedulerPropertiesConfig> {
    private final ContactSyncService contactSyncService;


    public ContactSyncScheduler(ContactSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                ContactSyncService contactSyncService) {
        super(CONTACT, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
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