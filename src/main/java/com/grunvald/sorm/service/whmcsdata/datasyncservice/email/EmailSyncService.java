package com.grunvald.sorm.service.whmcsdata.datasyncservice.email;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;


public interface EmailSyncService {

    Mono<EmailSyncPage> getActualData(Long clintCursorById, int limit, long applyDataLimit,
                                      OffsetDateTime historyDepthDate, List<String> maskedPatterns);

    Mono<Boolean> applyActualData(EmailSyncPage data);
}