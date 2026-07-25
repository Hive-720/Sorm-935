package com.grunvald.sorm.service.whmcsdata.datasyncservice.client;

import reactor.core.publisher.Mono;

public interface ClientSyncService {

    Mono<ClientSyncPage> getActual(long offset, int limit);

    Mono<Boolean> applyActualData(ClientSyncPage page);

    Mono<ClientDetailsSyncPage> getActualClientDetails(Long clientCursorById, int clientLimit, int concurrency);

    Mono<Boolean> applyActualData(ClientDetailsSyncPage page);
}