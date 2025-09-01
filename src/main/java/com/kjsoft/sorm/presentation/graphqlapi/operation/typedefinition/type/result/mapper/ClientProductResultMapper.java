package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.dto.ClientProductSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientProductResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientProductPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = ClientProductPresentationMapper.class)
public interface ClientProductResultMapper {

    @Mapping(target = "result", source = "clientProductList")
    ClientProductResult map(ClientProductSearchResult result);
}