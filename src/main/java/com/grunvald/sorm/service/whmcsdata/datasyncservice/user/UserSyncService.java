package com.grunvald.sorm.service.whmcsdata.datasyncservice.user;

import reactor.core.publisher.Mono;


public interface UserSyncService {

    Mono<UserSyncPage> getActualData(long offset, int limit);

    Mono<Boolean> applyActualData(UserSyncPage page);
}