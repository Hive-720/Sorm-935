package com.kjsoft.sorm.presentation.graphqlapi.operation.queryresolver;

import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.AffiliateService;
import com.kjsoft.sorm.graphql.generated.model.AffiliateFilterInputGql;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.AffiliateFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.AffiliateResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.AffiliateResultMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Affiliate;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class AffiliateResolver {
    private final AffiliateService affiliateService;
    private final AffiliateResultMapper affiliateResultMapper;
    private final AffiliateFilterPresentationMapper affiliateFilterMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<AffiliateResult> affiliates(@Argument AffiliateFilterInputGql filter, @Argument Integer offset,
                                            @Argument Integer limit, @Argument String cursor) {
        var searchFilter = affiliateFilterMapper.map(filter);
        return affiliateService.search(searchFilter, cursor, offset, limit)
                .map(affiliateResultMapper::map);
    }

    @SchemaMapping(typeName = "Affiliate", field = "client")
    public Mono<Client> getClientForAffiliate(Affiliate affiliate, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(affiliate.clientId()));
    }
}