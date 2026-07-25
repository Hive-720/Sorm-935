package com.grunvald.sorm.service.whmcsdata.dataservice.client;

import com.grunvald.sorm.service.whmcsdata.filter.ClientGroupFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientLoginFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.*;
import com.grunvald.sorm.service.whmcsdata.filter.ClientFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientProductFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface ClientRepository {

    Flux<ClientGroup> findAllClientGroup();

    Mono<Boolean> addAllClient(List<Client> clients);

    Mono<Boolean> addAllClientGroup(List<ClientGroup> clientGroups);

    Mono<Boolean> updateAllClientGroup(List<ClientGroup> clientGroups);

    Mono<Boolean> softDeleteAllClient(Set<Long> clientIds);

    Mono<Boolean> addAllClientProduct(List<ClientProduct> clientProducts);

    Mono<Boolean> updateAllClientProduct(List<ClientProduct> clientProducts);

    Flux<ClientLogin> findAllClientLastLoginByClientIdIn(Set<Long> clientIds);

    Flux<ClientProduct> findAllClientProductByIdIn(Set<Long> clientProductIds);

    Mono<Boolean> updateAllClient(List<Client> clients);

    Mono<Boolean> addAllClientLogin(List<ClientLogin> clientLogins);

    Flux<Client> findAllClientByIdIn(Set<Long> ids);

    Mono<ClientIdsResult> findAll(Long cursorById, int limit);

    Flux<ClientGroup> findAllClientGroupByIdIn(Set<Long> ids);

    Mono<ClientSearchResult> findAllClientByFilter(ClientFilter filter, String cursor, Integer offset, Integer limit);

    Mono<ClientLoginSearchResult> findAllClientLoginByFilter(ClientLoginFilter filter, String cursor, Integer offset, Integer limit);

    Mono<ClientGroupSearchResult> findAllClientGroupByFilter(ClientGroupFilter filter, String cursor, Integer offset, Integer limit);

    Mono<ClientProductSearchResult> findAllClientProductByFilter(ClientProductFilter filter, String cursor, Integer offset, Integer limit);
}