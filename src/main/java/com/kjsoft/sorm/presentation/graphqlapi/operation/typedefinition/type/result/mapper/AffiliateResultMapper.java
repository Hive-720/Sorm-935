package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.dto.AffiliateSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.AffiliateResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.AffiliatePresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = AffiliatePresentationMapper.class)
public interface AffiliateResultMapper {

    @Mapping(target = "result", source = "affiliateList")
    AffiliateResult map(AffiliateSearchResult result);
}