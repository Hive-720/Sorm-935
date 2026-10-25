package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.contact.ContactSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ContactDeleteSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SyncTaskMetrics;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CONTACT_DELETE;


@Component
@ConditionalOnBean(ContactDeleteSchedulerPropertiesConfig.class)
public class ContactDeleteSyncScheduler extends Scheduler<Boolean, ContactDeleteSchedulerPropertiesConfig> {
    private final ContactSyncService contactSyncService;


    public ContactDeleteSyncScheduler(ContactDeleteSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                      SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                      TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                      ContactSyncService contactSyncService, SyncTaskMetrics syncTaskMetrics) {
        super(CONTACT_DELETE, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator, syncTaskMetrics);
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