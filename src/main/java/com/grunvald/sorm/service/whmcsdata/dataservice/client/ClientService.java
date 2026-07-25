package com.grunvald.sorm.service.whmcsdata.dataservice.client;

import com.grunvald.sorm.service.whmcsdata.filter.ClientLoginFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.*;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import com.grunvald.sorm.service.whmcsdata.filter.ClientGroupFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientProductFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface ClientService {

    Mono<List<Client>> getAllClientByIds(Set<Long> ids);

    Mono<ClientIdsResult> getAllClient(Long cursorById, Integer limit);

    Mono<List<ClientGroup>> getAllClientGroupByIds(Set<Long> ids);

    Mono<Boolean> saveOrUpdateAllClientGroup(List<ClientGroup> list);

    Mono<Boolean> saveAllClient(List<Client> clients);

    Mono<Boolean> updateAllClient(List<Client> clients);

    Mono<Boolean> saveOrUpdateAllClientProducts(List<ClientProduct> list);

    Mono<Boolean> saveAllClientLogin(List<ClientLogin> clientLogin);

    Mono<Boolean> removeAllClient(Set<Long> ids);

    Mono<List<ClientLogin>> getLatestLastLoginAllByClientId(Set<Long> clientIds);

    Mono<ClientSearchResult> searchClients(ClientFilter filter, String cursor, Integer offset, Integer limit);

    Mono<ClientProductSearchResult> searchClientProducts(ClientProductFilter filter, String cursor, Integer offset, Integer limit);

    Mono<ClientGroupSearchResult> searchClientGroups(ClientGroupFilter filter, String cursor, Integer offset, Integer limit);

    Mono<ClientLoginSearchResult> searchClientLogins(ClientLoginFilter filter, String cursor, Integer offset, Integer limit);
}