package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.dto.ClientSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = ClientPresentationMapper.class)
public interface ClientResultMapper {

    @Mapping(target = "result", source = "clientList")
    ClientResult map(ClientSearchResult result);
}