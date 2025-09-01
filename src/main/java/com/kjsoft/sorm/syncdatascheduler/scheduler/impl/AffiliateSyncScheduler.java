package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.affiliate.AffiliateSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.affiliate.AffiliateSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.AffiliateSchedulerPropertiesConfig;
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
@ConditionalOnBean(AffiliateSchedulerPropertiesConfig.class)
public class AffiliateSyncScheduler extends Scheduler<AffiliateSyncPage, AffiliateSchedulerPropertiesConfig> {
    private final AffiliateSyncService affiliateSyncService;


    public AffiliateSyncScheduler(AffiliateSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                  SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                  TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                  AffiliateSyncService affiliateSyncService) {
        super(config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
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