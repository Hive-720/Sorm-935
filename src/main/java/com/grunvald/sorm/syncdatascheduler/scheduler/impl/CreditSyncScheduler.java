package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.credit.CreditSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.credit.CreditSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.CreditSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
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

import java.util.Set;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;
import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CREDIT;


@Component
@ConditionalOnBean(CreditSchedulerPropertiesConfig.class)
public class CreditSyncScheduler extends Scheduler<CreditSyncPage, CreditSchedulerPropertiesConfig> {
    private final CreditSyncService creditSyncService;


    public CreditSyncScheduler(CreditSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                               SchedulerTaskRepository schedulerTaskRepository, DateUtils date,
                               TransactionalOperator transactional, SchedulerCoordinator coordinator,
                               CreditSyncService creditSyncService, SyncTaskMetrics syncTaskMetrics) {
        super(CREDIT, config, schedulerHelper, schedulerTaskRepository, date, transactional, coordinator, syncTaskMetrics);
        this.creditSyncService = creditSyncService;
    }

    @Override
    protected Mono<Step<CreditSyncPage>> executeStep(Long ignored, String cursor) {
        var historyDepthDate = date.now().minusYears(config.getMaxAgeYears());
        Long cursorLong = cursor != null ? Long.parseLong(cursor) : null;
        return creditSyncService.getActualData(cursorLong, config.getClientLimit(),
                        config.getMaxClientCreditPerTask(), historyDepthDate)
                .map(page -> new Step<>(
                                page,
                                page.isHasNextPage(),
                                null,
                                page.getCursor() != null ? String.valueOf(page.getCursor()) : null
                        )
                );
    }

    @Override
    protected Mono<Boolean> afterStep(CreditSyncPage page) {
        return creditSyncService.applyActualData(page);
    }

    @Override
    protected Set<SchedulerType> getDependencies() {
        return Set.of(CLIENT);
    }
}