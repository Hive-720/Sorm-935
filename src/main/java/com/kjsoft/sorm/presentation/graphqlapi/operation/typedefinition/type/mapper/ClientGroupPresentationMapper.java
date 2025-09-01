package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientGroup;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface ClientGroupPresentationMapper {

    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    ClientGroup map(com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientGroup clientGroup);
}