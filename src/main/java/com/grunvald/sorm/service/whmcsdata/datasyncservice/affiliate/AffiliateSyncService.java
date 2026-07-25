package com.grunvald.sorm.service.whmcsdata.datasyncservice.affiliate;

import reactor.core.publisher.Mono;


public interface AffiliateSyncService {

    Mono<AffiliateSyncPage> getActualData(long offset, int limit);

    Mono<Boolean> applyActualData(AffiliateSyncPage page);
}