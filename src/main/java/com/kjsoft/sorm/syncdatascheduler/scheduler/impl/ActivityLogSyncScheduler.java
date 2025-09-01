package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.activitylog.ActivityLogSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.activitylog.ActivityLogSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.ActivityLogSchedulerPropertiesConfig;
import com.kjsoft.sorm.syncdatascheduler.task.model.SchedulerTask;
import com.kjsoft.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.kjsoft.sorm.syncdatascheduler.scheduler.Scheduler;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.kjsoft.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;


@Component
@ConditionalOnBean(ActivityLogSchedulerPropertiesConfig.class)
public class ActivityLogSyncScheduler extends Scheduler<ActivityLogSyncPage, ActivityLogSchedulerPropertiesConfig> {
    private final ActivityLogSyncService activityLogSyncService;


    public ActivityLogSyncScheduler(ActivityLogSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                    SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                    TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                    ActivityLogSyncService activityLogSyncService) {
        super(config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
        this.activityLogSyncService = activityLogSyncService;
    }

    @Override
    protected Mono<SchedulerTask> getTaskForProcessByCron() {
        return super.getTaskForProcessByCron()
                .flatMap(syncTask -> {
                    if (syncTask.getCursor() == null) {
                        return activityLogSyncService.getLatestCursor()
                                .doOnNext(cursor -> syncTask.setCursor(cursor.toString()))
                                .thenReturn(syncTask);
                    }
                    return Mono.just(syncTask);
                });
    }

    @Override
    protected Mono<Step<ActivityLogSyncPage>> executeStep(Long offset, String cursor) {
        return activityLogSyncService.getActualData(offset, config.getLimit(), getMinDate(cursor), config.getForbiddenDescriptionPatterns())
                .map(page -> new Step<>(page, page.isHasNextPage(), page.getNextOffset(), cursor));
    }

    @Override
    protected Mono<Boolean> afterStep(ActivityLogSyncPage page) {
        return activityLogSyncService.applyActualData(page);
    }

    private OffsetDateTime getMinDate(String cursorDateStr) {
        var minTime = date.now().minusYears(config.getMaxAgeYears());
        if (cursorDateStr == null) {
            return minTime;
        }

        var cursorDate = OffsetDateTime.parse(cursorDateStr);
        return minTime.isAfter(cursorDate) ? minTime : cursorDate;
    }
}