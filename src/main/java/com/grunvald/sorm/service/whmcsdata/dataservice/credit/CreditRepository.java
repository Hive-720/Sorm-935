package com.grunvald.sorm.service.whmcsdata.dataservice.credit;

import com.grunvald.sorm.service.whmcsdata.filter.CreditFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.dto.CreditSearchResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface CreditRepository {

    Mono<Boolean> addAll(List<Credit> creditList);

    Flux<Credit> findLatestAllByClientId(Set<Long> clientIds);

    Flux<Credit> findAllByIdIn(Set<Long> ids);

    Mono<CreditSearchResult> findAllByFilter(CreditFilter filter, String cursor, Integer offset, Integer limit);
}