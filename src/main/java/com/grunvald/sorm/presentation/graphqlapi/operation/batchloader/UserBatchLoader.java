package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.service.whmcsdata.dataservice.user.UserService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.UserPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.User;
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
public class UserBatchLoader implements MappedBatchLoader<Long, User> {
    private final UserService service;
    private final UserPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, User>> load(@NonNull Set<Long> userIds) {
        return service.getAllByIds(userIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collectMap(User::id)
                .toFuture();
    }
}