package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Transaction;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface TransactionPresentationMapper {

    @Mapping(target = "date", source = "date", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Transaction map(com.kjsoft.sorm.service.whmcsdata.dataservice.transaction.model.Transaction transaction);
}