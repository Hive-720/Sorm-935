package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Credit;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface CreditPresentationMapper {

    @Mapping(target = "date", source = "date", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Credit map(com.kjsoft.sorm.service.whmcsdata.dataservice.credit.model.Credit credit);
}