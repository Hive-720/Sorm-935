package com.grunvald.sorm.service.whmcsdata.dataservice.email;

import com.grunvald.sorm.service.whmcsdata.filter.EmailFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.dto.EmailSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface EmailRepository {

    Mono<Boolean> addAll(List<Email> emails);

    Flux<Email> getLatestAllByClientId(Set<Long> clientIds);

    Mono<EmailSearchResult> findAllByFilter(EmailFilter filter, String cursor, Integer offset, Integer limit);
}