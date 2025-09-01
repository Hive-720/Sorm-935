package com.kjsoft.sorm.service.whmcsdata.datasyncservice.affiliate;

import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.AffiliateService;
import com.kjsoft.sorm.service.whmcsprovider.dto.mapper.AffiliateMapper;
import com.kjsoft.sorm.service.whmcsprovider.WhmcsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class AffiliateSyncServiceImpl implements AffiliateSyncService {
    private final AffiliateService affiliateService;
    private final WhmcsProvider provider;
    private final AffiliateMapper mapper;


    @Override
    public Mono<AffiliateSyncPage> getActualData(long offset, int limit) {
        return provider.getAffiliates(offset, limit)
                .flatMap(result -> Flux.fromIterable(result.getElements())
                        .map(mapper::map)
                        .collectList()
                        .map(list -> AffiliateSyncPage.builder()
                                .elements(list)
                                .hasNextPage(result.isHasNextPage())
                                .nextOffset(result.getNextOffset())
                                .build()
                        )
                );
    }

    @Override
    public Mono<Boolean> applyActualData(AffiliateSyncPage page) {
        return affiliateService.saveOrUpdateAll(page.getElements());
    }
}