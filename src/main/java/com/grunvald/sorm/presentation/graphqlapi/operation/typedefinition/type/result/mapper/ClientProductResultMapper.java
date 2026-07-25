package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientProductSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientProductResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientProductPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = ClientProductPresentationMapper.class)
public interface ClientProductResultMapper {

    @Mapping(target = "result", source = "clientProductList")
    ClientProductResult map(ClientProductSearchResult result);
}