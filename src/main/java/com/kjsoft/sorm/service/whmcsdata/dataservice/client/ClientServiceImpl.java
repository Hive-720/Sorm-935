package com.kjsoft.sorm.service.whmcsdata.dataservice.client;

import com.kjsoft.sorm.service.whmcsdata.filter.ClientGroupFilter;
import com.kjsoft.sorm.service.whmcsdata.filter.ClientLoginFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.dto.*;
import com.kjsoft.sorm.service.whmcsdata.filter.ClientFilter;
import com.kjsoft.sorm.service.whmcsdata.filter.ClientProductFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import com.kjsoft.sorm.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final DateUtils date;
    private final ClientRepository repository;


    @Override
    public Mono<List<Client>> getAllClientByIds(@NonNull Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllClientByIdIn(ids)
                .collectList();
    }

    @Override
    public Mono<ClientIdsResult> getAllClient(Long cursorById, Integer limit) {
        return repository.findAll(cursorById, limit);
    }

    @Override
    public Mono<List<ClientGroup>> getAllClientGroupByIds(@NonNull Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllClientGroupByIdIn(ids)
                .collectList();
    }

    @Override
    public Mono<ClientSearchResult> searchClients(ClientFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllClientByFilter(filter, cursor, offset, limit);
    }

    @Override
    public Mono<ClientProductSearchResult> searchClientProducts(ClientProductFilter filter, String cursor,
                                                                Integer offset, Integer limit) {
        return repository.findAllClientProductByFilter(filter, cursor, offset, limit);
    }

    @Override
    public Mono<ClientGroupSearchResult> searchClientGroups(ClientGroupFilter filter, String cursor,
                                                            Integer offset, Integer limit) {
        return repository.findAllClientGroupByFilter(filter, cursor, offset, limit);
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAllClientGroup(@NonNull List<ClientGroup> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        return repository.findAllClientGroup()
                .collectMap(ClientGroup::getId)
                .flatMap(existingMap -> {

                    var toAdd = new ArrayList<ClientGroup>();
                    var toUpdate = new ArrayList<ClientGroup>();

                    for (var actual : incomingList) {
                        var local = existingMap.remove(actual.getId());

                        if (local == null) {
                            actual.setUpdatedAt(date.now());
                            toAdd.add(actual);
                        } else {
                            if (local.update(actual)) {
                                local.setUpdatedAt(date.now());
                                toUpdate.add(local);
                            }
                        }
                    }

                    if (!existingMap.isEmpty()) {
                        var toSoftDelete = existingMap.values()
                                .stream()
                                .peek(r -> r.setDeleted(true))
                                .toList();

                        toUpdate.addAll(toSoftDelete);
                    }

                    return (toAdd.isEmpty() ? Mono.just(true) : repository.addAllClientGroup(toAdd))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAllClientGroup(toUpdate))
                            .then(Mono.just(true));
                });
    }

    @Override
    public Mono<Boolean> saveAllClient(@NonNull List<Client> clients) {
        if (clients.isEmpty()) {
            return Mono.just(true);
        }

        return repository.findAllClientByIdIn(extractClientIds(clients))
                .collectMap(Client::getId)
                .flatMap(existingMap -> Flux.fromIterable(clients)
                        .filter(client -> !existingMap.containsKey(client.getId()))
                        .collectList()
                        .filter(list -> !list.isEmpty())
                        .flatMap(repository::addAllClient))
                .switchIfEmpty(Mono.just(true));
    }

    @Override
    public Mono<Boolean> updateAllClient(@NonNull List<Client> clients) {
        if (clients.isEmpty()) {
            return Mono.just(true);
        }

        var actualClients = toMap(clients);
        return repository.findAllClientByIdIn(actualClients.keySet())
                .filter(existing -> actualClients.containsKey(existing.getId()))
                .filter(existing -> existing.update(actualClients.get(existing.getId())))
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMap(repository::updateAllClient)
                .switchIfEmpty(Mono.just(true));
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAllClientProducts(@NonNull List<ClientProduct> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        var ids = extractClientProductIds(incomingList);

        return repository.findAllClientProductByIdIn(ids)
                .collectMap(ClientProduct::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<ClientProduct>();
                    var toUpdate = new ArrayList<ClientProduct>();

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

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAllClientProduct(toSave))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAllClientProduct(toUpdate))
                            .then(Mono.just(true));
                });
    }

    @Override
    public Mono<Boolean> saveAllClientLogin(@NonNull List<ClientLogin> clientLogin) {
        if (clientLogin.isEmpty()) {
            return Mono.just(true);
        }

        return repository.addAllClientLogin(clientLogin);
    }

    @Override
    public Mono<Boolean> removeAllClient(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(true);
        }

        return repository.softDeleteAllClient(ids);
    }

    @Override
    public Mono<List<ClientLogin>> getLatestLastLoginAllByClientId(@NonNull Set<Long> clientIds) {
        if (clientIds.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllClientLastLoginByClientIdIn(clientIds)
                .collectList();
    }

    @Override
    public Mono<ClientLoginSearchResult> searchClientLogins(ClientLoginFilter filter, String cursor,
                                                            Integer offset, Integer limit) {
        return repository.findAllClientLoginByFilter(filter, cursor, offset, limit);
    }

    private Set<Long> extractClientIds(List<Client> clients) {
        return clients.stream()
                .map(Client::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> extractClientProductIds(List<ClientProduct> clientProducts) {
        return clientProducts.stream()
                .map(ClientProduct::getId)
                .collect(Collectors.toSet());
    }

    private Map<Long, Client> toMap(List<Client> clients) {
        return clients.stream()
                .collect(Collectors.toMap(Client::getId, Function.identity()));
    }
}