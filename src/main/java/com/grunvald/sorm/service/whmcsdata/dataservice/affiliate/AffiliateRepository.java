package com.grunvald.sorm.service.whmcsdata.dataservice.affiliate;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.dto.AffiliateSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface AffiliateRepository {

    Mono<Boolean> addAll(List<Affiliate> affiliates);

    Mono<Boolean> updateAll(List<Affiliate> updates);

    Flux<Affiliate> findAllByIdIn(Set<Long> ids);

    Mono<AffiliateSearchResult> findAllAffiliateByFilter(AffiliateFilter filter, String cursor, Integer offset, Integer limit);
}