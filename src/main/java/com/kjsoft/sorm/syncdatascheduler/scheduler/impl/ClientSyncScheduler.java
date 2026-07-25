package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.client.ClientSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.client.ClientSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.ClientSchedulerPropertiesConfig;
import com.kjsoft.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.kjsoft.sorm.syncdatascheduler.scheduler.Scheduler;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.kjsoft.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;


@Component
@ConditionalOnBean(ClientSchedulerPropertiesConfig.class)
public class ClientSyncScheduler extends Scheduler<ClientSyncPage, ClientSchedulerPropertiesConfig> {
    private final ClientSyncService clientSyncService;


    public ClientSyncScheduler(ClientSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                               SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                               TransactionalOperator transactional, SchedulerCoordinator coordinator,
                               ClientSyncService clientSyncService) {
        super(CLIENT, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
        this.clientSyncService = clientSyncService;
    }

    @Override
    protected Mono<Step<ClientSyncPage>> executeStep(Long offset, String ignored) {
        return clientSyncService.getActual(offset, config.getLimit())
                .map(page -> new Step<>(page, page.isHasNextPage(), page.getNextOffset(), null));
    }

    @Override
    protected Mono<Boolean> afterStep(ClientSyncPage page) {
        return clientSyncService.applyActualData(page);
    }
}