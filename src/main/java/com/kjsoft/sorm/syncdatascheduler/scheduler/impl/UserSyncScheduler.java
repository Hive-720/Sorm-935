package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.user.UserSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.user.UserSyncService;
import com.kjsoft.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.kjsoft.sorm.syncdatascheduler.config.impl.UserSchedulerPropertiesConfig;
import com.kjsoft.sorm.syncdatascheduler.scheduler.Scheduler;
import com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.kjsoft.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;
import static com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType.USER;


@Component
@ConditionalOnBean(UserSchedulerPropertiesConfig.class)
public class UserSyncScheduler extends Scheduler<UserSyncPage, UserSchedulerPropertiesConfig> {
    private final UserSyncService userSyncService;


    public UserSyncScheduler(UserSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                             SchedulerTaskRepository taskRepository, DateUtils dateTime, TransactionalOperator transactional,
                             SchedulerCoordinator coordinator, UserSyncService userSyncService) {
        super(USER, config, schedulerHelper, taskRepository, dateTime, transactional, coordinator);
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