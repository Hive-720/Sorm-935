package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.dto.CreditSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.CreditResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.CreditPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = CreditPresentationMapper.class)
public interface CreditResultMapper {

    @Mapping(target = "result", source = "creditList")
    CreditResult map(CreditSearchResult result);
}