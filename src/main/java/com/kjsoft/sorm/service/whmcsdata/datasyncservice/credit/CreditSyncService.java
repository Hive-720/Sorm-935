package com.kjsoft.sorm.service.whmcsdata.datasyncservice.credit;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;


public interface CreditSyncService {

    Mono<CreditSyncPage> getActualData(Long clintCursorById, int limit, long applyDataLimit,
                                       OffsetDateTime historyDepthDate);

    Mono<Boolean> applyActualData(CreditSyncPage page);
}