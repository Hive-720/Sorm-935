package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.PaymentMethod;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface PaymentMethodPresentationMapper {

    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "moduleName", source = "module")
    PaymentMethod map(com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod paymentMethod);
}