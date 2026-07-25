package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.client.ClientDetailsSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.client.ClientSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.ClientDetailSchedulerPropertiesConfig;
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
import static com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT_DETAIL;


@Component
@ConditionalOnBean(ClientDetailSchedulerPropertiesConfig.class)
public class ClientDetailSyncScheduler extends Scheduler<ClientDetailsSyncPage, ClientDetailSchedulerPropertiesConfig> {
    private final ClientSyncService clientSyncService;


    public ClientDetailSyncScheduler(ClientDetailSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                     SchedulerTaskRepository schedulerTaskRepository, DateUtils date,
                                     TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                     ClientSyncService clientSyncService) {
        super(CLIENT_DETAIL, config, schedulerHelper, schedulerTaskRepository, date, transactional, coordinator);
        this.clientSyncService = clientSyncService;
    }

    @Override
    protected Mono<Step<ClientDetailsSyncPage>> executeStep(Long ignored, String clientCursor) {
        var clientCursorLong = clientCursor != null ? Long.parseLong(clientCursor) : null;
        return clientSyncService.getActualClientDetails(clientCursorLong, config.getClientLimit(), config.getConcurrency())
                .map(page -> new Step<>(
                                page,
                                page.isHasNextPage(),
                                null,
                                page.getCursor() != null ? String.valueOf(page.getCursor()) : null
                        )
                );
    }

    @Override
    protected Mono<Boolean> afterStep(ClientDetailsSyncPage page) {
        return clientSyncService.applyActualData(page);
    }

    @Override
    protected Set<SchedulerType> getDependencies() {
        return Set.of(CLIENT);
    }

}