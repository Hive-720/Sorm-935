package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.credit.CreditSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.credit.CreditSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.CreditSchedulerPropertiesConfig;
import com.kjsoft.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.kjsoft.sorm.syncdatascheduler.scheduler.Scheduler;
import com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.kjsoft.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;


@Component
@ConditionalOnBean(CreditSchedulerPropertiesConfig.class)
public class CreditSyncScheduler extends Scheduler<CreditSyncPage, CreditSchedulerPropertiesConfig> {
    private final CreditSyncService creditSyncService;


    public CreditSyncScheduler(CreditSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                               SchedulerTaskRepository schedulerTaskRepository, DateUtils date,
                               TransactionalOperator transactional, SchedulerCoordinator coordinator,
                               CreditSyncService creditSyncService) {
        super(config, schedulerHelper, schedulerTaskRepository, date, transactional, coordinator);
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