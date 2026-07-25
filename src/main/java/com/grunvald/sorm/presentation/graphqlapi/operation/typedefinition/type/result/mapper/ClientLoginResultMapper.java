package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientLoginPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientLoginResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientLoginSearchResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = ClientLoginPresentationMapper.class)
public interface ClientLoginResultMapper {

    @Mapping(target = "result", source = "clientLoginList")
    ClientLoginResult map(ClientLoginSearchResult result);
}