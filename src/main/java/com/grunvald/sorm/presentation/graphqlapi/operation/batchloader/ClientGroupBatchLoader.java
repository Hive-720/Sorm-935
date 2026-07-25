package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientGroupPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientGroup;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;


@Component
@RequiredArgsConstructor
public class ClientGroupBatchLoader implements MappedBatchLoader<Long, ClientGroup> {
    private final ClientService service;
    private final ClientGroupPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, ClientGroup>> load(@NonNull Set<Long> clientGroupIds) {
        return service.getAllClientGroupByIds(clientGroupIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collectMap(ClientGroup::id)
                .toFuture();
    }
}