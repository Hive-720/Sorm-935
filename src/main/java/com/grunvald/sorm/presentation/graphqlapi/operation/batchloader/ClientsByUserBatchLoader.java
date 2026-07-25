package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientPresentationMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.UserService;
import lombok.AllArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class ClientsByUserBatchLoader implements MappedBatchLoader<Long, List<Client>> {
    private final UserService userService;
    private final ClientPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, List<Client>>> load(@NonNull Set<Long> userIds) {
        return userService.getGroupingClientsByUser(userIds)
                .map(clientsMap -> clientsMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().stream()
                                        .map(mapper::map)
                                        .collect(Collectors.toList())
                        ))
                )
                .toFuture();
    }
}