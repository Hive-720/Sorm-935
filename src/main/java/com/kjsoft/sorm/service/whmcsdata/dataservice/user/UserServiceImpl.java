package com.kjsoft.sorm.service.whmcsdata.dataservice.user;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.kjsoft.sorm.service.whmcsdata.dataservice.user.dto.UserSearchResult;
import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.User;
import com.kjsoft.sorm.service.whmcsdata.filter.UserFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final ClientService clientService;


    @Override
    public Mono<List<User>> getAllByIds(@NonNull Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllByIdIn(ids)
                .collectList();
    }

    @Override
    public Mono<Map<Long, List<Client>>> getGroupingClientsByUser(@NonNull Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }

        return repository.findAllRelationsByUserIdIn(userIds)
                .collectMultimap(
                        UserClientRelation::getUserId,
                        UserClientRelation::getClientId
                )
                .flatMap(userToClientIdsMap -> {
                    if (userToClientIdsMap.isEmpty()) {
                        return Mono.just(Collections.emptyMap());
                    }

                    var clientIds = userToClientIdsMap.values().stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.toSet());

                    return clientService.getAllClientByIds(clientIds)
                            .flatMapMany(Flux::fromIterable)
                            .collectMap(Client::getId, Function.identity())
                            .map(clientMap -> userToClientIdsMap.entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            entry -> entry.getValue().stream()
                                                    .map(clientMap::get)
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList())
                                    )));
                });
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAll(@NonNull List<User> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        var ids = extractIds(incomingList);
        return repository.findAllByIdIn(ids)
                .collectMap(User::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<User>();
                    var toUpdate = new ArrayList<User>();

                    for (var actual : incomingList) {
                        var local = existingMap.get(actual.getId());

                        if (local == null) {
                            toSave.add(actual);
                        } else {
                            if (local.update(actual)) {
                                toUpdate.add(local);
                            }
                        }
                    }

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAll(toSave))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAll(toUpdate))
                            .then(Mono.just(true));
                });
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrRemoveRelationsAll(@NonNull List<UserClientRelation> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        var pair = decomposeRelationsOnIds(incomingList);
        return Mono.zip(clientService.getAllClientByIds(pair.getLeft())
                                .flatMapMany(Flux::fromIterable)
                                .map(Client::getId)
                                .collect(Collectors.toSet()),
                        repository.findAllRelationsByUserIdIn(pair.getRight())
                                .collect(Collectors.toSet())
                )
                .flatMap(tuple -> {
                    var existingClientIds = tuple.getT1();
                    var existingRelations = tuple.getT2();

                    var validIncomingRelations = incomingList.stream()
                            .filter(r -> existingClientIds.contains(r.getClientId()))
                            .collect(Collectors.toSet());

                    var toAdd = validIncomingRelations.stream()
                            .filter(r -> !existingRelations.contains(r))
                            .toList();
                    var toRemove = existingRelations.stream()
                            .filter(r -> !validIncomingRelations.contains(r))
                            .toList();

                    return Mono.when(
                                    toRemove.isEmpty() ? Mono.empty() : repository.deleteAllRelations(toRemove),
                                    toAdd.isEmpty() ? Mono.empty() : repository.addAllRelations(toAdd)
                            )
                            .thenReturn(true);
                });
    }

    private Pair<Set<Long>, Set<Long>> decomposeRelationsOnIds(@NonNull List<UserClientRelation> relations) {
        var clientIds = new HashSet<Long>();
        var userIds = new HashSet<Long>();

        for (var relation : relations) {
            if (relation.getClientId() != null) {
                clientIds.add(relation.getClientId());
            }
            if (relation.getUserId() != null) {
                userIds.add(relation.getUserId());
            }
        }

        return Pair.of(clientIds, userIds);
    }

    @Override
    public Mono<UserSearchResult> search(UserFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllByFilter(filter, cursor, offset, limit);
    }

    private Set<Long> extractIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }
}