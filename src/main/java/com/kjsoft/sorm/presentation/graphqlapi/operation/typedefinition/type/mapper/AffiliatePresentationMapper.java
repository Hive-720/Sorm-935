package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Affiliate;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface AffiliatePresentationMapper {

    @Mapping(target = "date", source = "date", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Affiliate map(com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate affiliate);
}