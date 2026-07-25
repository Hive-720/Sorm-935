package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Email;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface EmailPresentationMapper {

    @Mapping(target = "date", source = "date", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "to", source = "to", qualifiedByName = "stringToEmailBase")
    @Mapping(target = "cc", source = "cc", qualifiedByName = "stringListToEmailBaseList")
    @Mapping(target = "bcc", source = "bcc", qualifiedByName = "stringListToEmailBaseList")
    Email map(com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email email);
}