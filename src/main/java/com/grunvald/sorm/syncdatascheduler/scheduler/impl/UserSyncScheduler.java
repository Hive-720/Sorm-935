package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.user.UserSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.user.UserSyncService;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.config.impl.UserSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SyncTaskMetrics;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;
import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.USER;


@Component
@ConditionalOnBean(UserSchedulerPropertiesConfig.class)
public class UserSyncScheduler extends Scheduler<UserSyncPage, UserSchedulerPropertiesConfig> {
    private final UserSyncService userSyncService;


    public UserSyncScheduler(UserSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                             SchedulerTaskRepository taskRepository, DateUtils dateTime, TransactionalOperator transactional,
                             SchedulerCoordinator coordinator, UserSyncService userSyncService, SyncTaskMetrics syncTaskMetrics) {
        super(USER, config, schedulerHelper, taskRepository, dateTime, transactional, coordinator, syncTaskMetrics);
        this.userSyncService = userSyncService;
    }

    @Override
    protected Mono<Step<UserSyncPage>> executeStep(Long offset, String cursor) {
        return userSyncService.getActualData(offset, config.getLimit())
                .map(page -> new Step<>(page, page.isHasNextPage(), page.getNextOffset(), cursor));
    }

    @Override
    protected Mono<Boolean> afterStep(UserSyncPage page) {
        return userSyncService.applyActualData(page);
    }

    @Override
    protected Set<SchedulerType> getDependencies() {
        return Set.of(CLIENT);
    }
}