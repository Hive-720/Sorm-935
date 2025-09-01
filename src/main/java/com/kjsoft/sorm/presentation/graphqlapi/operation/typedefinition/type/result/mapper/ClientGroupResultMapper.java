package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.dto.ClientGroupSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientGroupPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientGroupResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = ClientGroupPresentationMapper.class)
public interface ClientGroupResultMapper {

    @Mapping(target = "result", source = "clientGroupList")
    ClientGroupResult map(ClientGroupSearchResult result);
}