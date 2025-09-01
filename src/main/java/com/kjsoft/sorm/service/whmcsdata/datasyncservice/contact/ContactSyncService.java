package com.kjsoft.sorm.service.whmcsdata.datasyncservice.contact;

import reactor.core.publisher.Mono;


public interface ContactSyncService {

    Mono<ContactSyncPage> getActualData(long offset, int limit);

    Mono<Boolean> applyActualData(ContactSyncPage page);

    Mono<Boolean> delete(long offset, int limit);
}