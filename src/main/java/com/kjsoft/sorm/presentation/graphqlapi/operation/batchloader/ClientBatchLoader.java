package com.kjsoft.sorm.presentation.graphqlapi.operation.batchloader;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import lombok.AllArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;


@Component
@AllArgsConstructor
public class ClientBatchLoader implements MappedBatchLoader<Long, Client> {
    private final ClientService service;
    private final ClientPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, Client>> load(@NonNull Set<Long> clientIds) {
        return service.getAllClientByIds(clientIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collectMap(Client::id)
                .toFuture();
    }
}