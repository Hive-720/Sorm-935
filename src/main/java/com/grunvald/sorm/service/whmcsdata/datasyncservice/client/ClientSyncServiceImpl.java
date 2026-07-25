package com.grunvald.sorm.service.whmcsdata.datasyncservice.client;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ClientMapper;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProviderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class ClientSyncServiceImpl implements ClientSyncService {
    private final ClientService clientService;
    private final WhmcsProvider whmcs;
    private final ClientMapper clientMapper;


    @Override
    public Mono<ClientSyncPage> getActual(long offset, int limit) {
        return whmcs.getClients(offset, limit)
                .flatMap(result -> Flux.fromIterable(result.getElements())
                        .map(clientMapper::map)
                        .collectList()
                        .map(list -> ClientSyncPage.builder()
                                .elements(list)
                                .hasNextPage(result.isHasNextPage())
                                .nextOffset(result.getNextOffset())
                                .build()
                        )
                );
    }

    @Override
    public Mono<Boolean> applyActualData(ClientSyncPage page) {
        return clientService.saveAllClient(page.getElements());
    }

    @Override
    public Mono<ClientDetailsSyncPage> getActualClientDetails(Long clientCursorById, int clientLimit, int concurrency) {
        return clientService.getAllClient(clientCursorById, clientLimit)
                .flatMap(searchResult -> Flux.fromIterable(searchResult.clientIds())
                        .collect(Collectors.toSet())
                        .filter(ids -> !ids.isEmpty())
                        .flatMap(clientIds -> clientService.getLatestLastLoginAllByClientId(clientIds)
                                .flatMapMany(Flux::fromIterable)
                                .collectMap(ClientLogin::getClientId, ClientLogin::getDate)
                                .flatMap(latestLastLoginMap -> Flux.fromIterable(clientIds)
                                        .flatMap(clientId -> whmcs.getClientDetails(clientId)
                                                        .map(result -> {
                                                            if (result.isDeleted()) {
                                                                var client = new Client();
                                                                client.setId(clientId);
                                                                client.setDeleted(true);
                                                                return new ClientContainer(client, true);
                                                            }

                                                            var actualLastLogin = getClientLastLogin(result.getElement());
                                                            if (actualLastLogin != null) {
                                                                var latestDateLogin = latestLastLoginMap.get(clientId);
                                                                if (latestDateLogin != null && latestDateLogin.isAfter(actualLastLogin.getDate())) {
                                                                    actualLastLogin = null;
                                                                }

                                                            }

                                                            var client = clientMapper.map(result.getElement());
                                                            return new ClientContainer(client, actualLastLogin);
                                                        })
                                                , concurrency)
                                        .collect(() -> Tuples.of(new ArrayList<Client>(), new ArrayList<ClientLogin>(), new HashSet<Long>()),
                                                (t3, result) -> {
                                                    if (result.deleted) {
                                                        t3.getT3().add(result.client.getId());
                                                    } else if (result.client != null) {
                                                        t3.getT1().add(result.client);
                                                    }

                                                    if (result.lastLogin != null) {
                                                        t3.getT2().add(result.lastLogin);
                                                    }
                                                })
                                        .map(t4 -> new ClientDetailsSyncPage(t4.getT1(), t4.getT2(), t4.getT3(), searchResult.hasNextPage(), searchResult.clientIds().getLast()))
                                )
                        )
                        .switchIfEmpty(Mono.just(ClientDetailsSyncPage.EMPTY))
                );
    }

    @Override
    public Mono<Boolean> applyActualData(ClientDetailsSyncPage page) {
        return clientService.updateAllClient(page.getActual())
                .then(clientService.saveAllClientLogin(page.getLastLogins()))
                .then(clientService.removeAllClient(page.getRemovedClients()));
    }

    private ClientLogin getClientLastLogin(ClientProviderDto dto) {
        var lastLoginDto = dto.getLastLogin();
        ClientLogin lastLogin = null;

        if (lastLoginDto != null && (lastLoginDto.getIpAddress() != null || lastLoginDto.getDate() != null)) {
            lastLogin = new ClientLogin(dto.getId(), lastLoginDto.getIpAddress(), lastLoginDto.getDate());
        }

        return lastLogin;
    }

    private static class ClientContainer {
        private final Client client;
        private ClientLogin lastLogin;
        private boolean deleted;

        private ClientContainer(Client client, ClientLogin lastLogin) {
            this.client = client;
            this.lastLogin = lastLogin;
        }

        private ClientContainer(Client client, boolean deleted) {
            this.client = client;
            this.deleted = deleted;
        }
    }
}