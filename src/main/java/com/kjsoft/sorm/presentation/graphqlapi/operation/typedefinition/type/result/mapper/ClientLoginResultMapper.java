package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientLoginPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientLoginResult;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.dto.ClientLoginSearchResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = ClientLoginPresentationMapper.class)
public interface ClientLoginResultMapper {

    @Mapping(target = "result", source = "clientLoginList")
    ClientLoginResult map(ClientLoginSearchResult result);
}