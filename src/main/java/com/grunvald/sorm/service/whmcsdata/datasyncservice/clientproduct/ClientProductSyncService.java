package com.grunvald.sorm.service.whmcsdata.datasyncservice.clientproduct;

import reactor.core.publisher.Mono;


public interface ClientProductSyncService {

    Mono<ClientProductSyncPage> getActualData(long offset, int limit);

    Mono<Boolean> applyActualData(ClientProductSyncPage page);
}