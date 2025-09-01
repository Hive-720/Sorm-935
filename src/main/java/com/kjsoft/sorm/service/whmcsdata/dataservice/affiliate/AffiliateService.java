package com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate;

import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.dto.AffiliateSearchResult;
import com.kjsoft.sorm.service.whmcsdata.filter.AffiliateFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface AffiliateService {

    Mono<List<Affiliate>> getAllByIds(Set<Long> ids);

    Mono<Boolean> saveOrUpdateAll(List<Affiliate> affiliateList);

    Mono<AffiliateSearchResult> search(AffiliateFilter filter, String cursor, Integer offset, Integer limit);
}