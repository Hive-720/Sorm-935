package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.contact.ContactSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.ContactDeleteSchedulerPropertiesConfig;
import com.kjsoft.sorm.syncdatascheduler.scheduler.Scheduler;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.kjsoft.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.kjsoft.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType.CONTACT_DELETE;


@Component
@ConditionalOnBean(ContactDeleteSchedulerPropertiesConfig.class)
public class ContactDeleteSyncScheduler extends Scheduler<Boolean, ContactDeleteSchedulerPropertiesConfig> {
    private final ContactSyncService contactSyncService;


    public ContactDeleteSyncScheduler(ContactDeleteSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                      SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                      TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                      ContactSyncService contactSyncService) {
        super(CONTACT_DELETE, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
        this.contactSyncService = contactSyncService;
    }

    @Override
    protected Mono<Step<Boolean>> executeStep(Long offset, String cursor) {
        return contactSyncService.delete(offset, config.getLimit())
                .map(bool -> new Step<>(bool, false, null, null));
    }

    @Override
    protected Mono<Boolean> afterStep(Boolean data) {
        return Mono.just(true);
    }

    @Override
    protected boolean saveStateTask() {
        return false;
    }
}