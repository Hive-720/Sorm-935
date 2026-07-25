package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.ActivityLogService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ActivityLogFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ActivityLogFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.User;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ActivityLogResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ActivityLogResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ActivityLog;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class ActivityLogResolver {
    private final ActivityLogService activityLogService;
    private final ActivityLogFilterPresentationMapper activityLogFilterMapper;
    private final ActivityLogResultMapper activityLogResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<ActivityLogResult> activityLog(@Argument ActivityLogFilterInput filter, @Argument Integer offset,
                                               @Argument Integer limit, @Argument String cursor) {
        var searchFilter = activityLogFilterMapper.map(filter);
        return activityLogService.search(searchFilter, cursor, offset, limit)
                .map(activityLogResultMapper::map);
    }

    @SchemaMapping(typeName = "ActivityLog", field = "client")
    public Mono<Client> getClientForActivityLog(ActivityLog activityLog, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(activityLog.clientId()));
    }

    @SchemaMapping(typeName = "ActivityLog", field = "user")
    public Mono<User> getUserForActivityLog(ActivityLog activityLog, DataLoader<Long, User> dataLoader) {
        return Mono.fromFuture(dataLoader.load(activityLog.userId()));
    }
}