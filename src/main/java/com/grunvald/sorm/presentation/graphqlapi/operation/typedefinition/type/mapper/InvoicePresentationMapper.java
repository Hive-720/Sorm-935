package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface InvoicePresentationMapper {

    @Mapping(target = "date", source = "date", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "dueDate", source = "dueDate", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "datePaid", source = "datePaid", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "dateRefunded", source = "dateRefunded", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "dateCancelled", source = "dateCancelled", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Invoice map(com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice invoice);
}