package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = ClientPresentationMapper.class)
public interface ClientResultMapper {

    @Mapping(target = "result", source = "clientList")
    ClientResult map(ClientSearchResult result);
}