package com.grunvald.sorm.service.whmcsdata.datasyncservice.invoice;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;


public interface InvoiceSyncService {

    Mono<InvoiceSyncPage> getActualData(long offset, int limit, OffsetDateTime historyDepthDate);

    Mono<Boolean> applyActualData(InvoiceSyncPage data);
}