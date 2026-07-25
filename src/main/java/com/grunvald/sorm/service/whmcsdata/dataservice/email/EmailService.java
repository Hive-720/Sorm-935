package com.grunvald.sorm.service.whmcsdata.dataservice.email;

import com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email;
import com.grunvald.sorm.service.whmcsdata.filter.EmailFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.dto.EmailSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface EmailService {

    Mono<EmailSearchResult> search(EmailFilter filter, String cursor, Integer offset, Integer limit);

    Mono<List<Email>> getLatestAllByClientId(Set<Long> clientIds);

    Mono<Boolean> saveAll(List<Email> emails);
}