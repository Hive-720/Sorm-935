package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.affiliate.AffiliateSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.affiliate.AffiliateSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.AffiliateSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.AFFILIATE;


@Component
@ConditionalOnBean(AffiliateSchedulerPropertiesConfig.class)
public class AffiliateSyncScheduler extends Scheduler<AffiliateSyncPage, AffiliateSchedulerPropertiesConfig> {
    private final AffiliateSyncService affiliateSyncService;


    public AffiliateSyncScheduler(AffiliateSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                  SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                  TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                  AffiliateSyncService affiliateSyncService) {
        super(AFFILIATE, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
        this.affiliateSyncService = affiliateSyncService;
    }

    @Override
    protected Mono<Step<AffiliateSyncPage>> executeStep(Long offset, String cursor) {
        return affiliateSyncService.getActualData(offset, config.getLimit())
                .map(page -> new Step<>(page, page.isHasNextPage(), page.getNextOffset(), cursor));
    }

    @Override
    protected Mono<Boolean> afterStep(AffiliateSyncPage page) {
        return affiliateSyncService.applyActualData(page);
    }
}