package com.grunvald.sorm.syncdatascheduler.scheduler.impl;

import com.grunvald.sorm.service.whmcsdata.datasyncservice.clientgroup.ClientGroupSyncService;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientGroupSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.scheduler.Scheduler;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.grunvald.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT_GROUP;


@Component
@ConditionalOnBean(ClientGroupSchedulerPropertiesConfig.class)
public class ClientGroupSyncScheduler extends Scheduler<List<ClientGroup>, ClientGroupSchedulerPropertiesConfig> {
    private final ClientGroupSyncService clientGroupSyncService;


    public ClientGroupSyncScheduler(ClientGroupSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                                    SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                                    TransactionalOperator transactional, SchedulerCoordinator coordinator,
                                    ClientGroupSyncService clientGroupSyncService) {
        super(CLIENT_GROUP, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
        this.clientGroupSyncService = clientGroupSyncService;
    }

    @Override
    protected Mono<Step<List<ClientGroup>>> executeStep(Long offset, String cursor) {
        return clientGroupSyncService.getActualData()
                .map(list -> new Step<>(list, false, offset, cursor));
    }

    @Override
    protected Mono<Boolean> afterStep(List<ClientGroup> data) {
        return clientGroupSyncService.applyActualData(data);
    }

    @Override
    protected boolean saveStateTask() {
        return false;
    }
}