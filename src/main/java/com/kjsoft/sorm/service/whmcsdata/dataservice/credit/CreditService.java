package com.kjsoft.sorm.service.whmcsdata.dataservice.credit;

import com.kjsoft.sorm.service.whmcsdata.filter.CreditFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.dto.CreditSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface CreditService {

    Mono<Boolean> saveAll(List<Credit> credits);

    Mono<List<Credit>> getLatestAllByClientId(Set<Long> clientId);

    Mono<CreditSearchResult> search(CreditFilter filter, String cursor, Integer offset, Integer limit);
}