package com.grunvald.sorm.service.whmcsdata.datasyncservice.order;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;


public interface OrderSyncService {

    Mono<OrderSyncPage> getActualData(long offset, int limit, OffsetDateTime historyDepthDate);

    Mono<Boolean> applyActualData(OrderSyncPage page);
}