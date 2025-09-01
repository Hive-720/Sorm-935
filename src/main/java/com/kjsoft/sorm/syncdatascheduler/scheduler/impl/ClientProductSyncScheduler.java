package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.clientproduct.ClientProductSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.clientproduct.ClientProductSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.ClientProductSchedulerPropertiesConfig;
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
@ConditionalOnBean(ClientProductSchedulerPropertiesConfig.class)
public class ClientProductSyncScheduler extends Scheduler<ClientProductSyncPage, ClientProductSchedulerPropertiesConfig> {
    private final ClientProductSyncService clientProductSyncService;


    public ClientProductSyncScheduler(ClientProductSchedulerPropertiesConfig properties, SchedulerHelper schedulerHelper,
                                      SchedulerTaskRepository schedulerTaskRepository, DateUtils date,
                                      TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                      ClientProductSyncService clientProductSyncService) {
        super(properties, schedulerHelper, schedulerTaskRepository, date, transactional, coordinator);
        this.clientProductSyncService = clientProductSyncService;
    }

    @Override
    protected Mono<Step<ClientProductSyncPage>> executeStep(Long offset, String cursor) {
        return clientProductSyncService.getActualData(offset, config.getLimit())
                .map(page -> new Step<>(page, page.isHasNextPage(), page.getNextOffset(), cursor));
    }

    @Override
    protected Mono<Boolean> afterStep(ClientProductSyncPage page) {
        return clientProductSyncService.applyActualData(page);
    }
}