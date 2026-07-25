package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.clientproduct.ClientProductSyncPage;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.clientproduct.ClientProductSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientProductSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT_PRODUCT;


@Component
@ConditionalOnBean(ClientProductSchedulerPropertiesConfig.class)
public class ClientProductSyncScheduler extends Scheduler<ClientProductSyncPage, ClientProductSchedulerPropertiesConfig> {
    private final ClientProductSyncService clientProductSyncService;


    public ClientProductSyncScheduler(ClientProductSchedulerPropertiesConfig properties, SchedulerHelper schedulerHelper,
                                      SchedulerTaskRepository schedulerTaskRepository, DateUtils date,
                                      TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                      ClientProductSyncService clientProductSyncService) {
        super(CLIENT_PRODUCT, properties, schedulerHelper, schedulerTaskRepository, date, transactional, coordinator);
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