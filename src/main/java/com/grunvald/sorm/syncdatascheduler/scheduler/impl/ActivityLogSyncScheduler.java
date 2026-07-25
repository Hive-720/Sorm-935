package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.activitylog.ActivityLogSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.activitylog.ActivityLogSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ActivityLogSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.ACTIVITY_LOG;


@Component
@ConditionalOnBean(ActivityLogSchedulerPropertiesConfig.class)
public class ActivityLogSyncScheduler extends Scheduler<ActivityLogSyncPage, ActivityLogSchedulerPropertiesConfig> {
    private final ActivityLogSyncService activityLogSyncService;


    public ActivityLogSyncScheduler(ActivityLogSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                    SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                    TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                    ActivityLogSyncService activityLogSyncService) {
        super(ACTIVITY_LOG, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
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