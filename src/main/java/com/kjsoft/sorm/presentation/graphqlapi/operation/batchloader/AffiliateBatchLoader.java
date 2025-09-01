package com.kjsoft.sorm.presentation.graphqlapi.operation.batchloader;

import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.AffiliateService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.AffiliatePresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Affiliate;
import lombok.AllArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;


@Component
@AllArgsConstructor
public class AffiliateBatchLoader implements MappedBatchLoader<Long, Affiliate> {
    private AffiliateService service;
    private AffiliatePresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, Affiliate>> load(@NonNull Set<Long> affiliateIds) {
        return service.getAllByIds(affiliateIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collectMap(Affiliate::id)
                .toFuture();
    }
}