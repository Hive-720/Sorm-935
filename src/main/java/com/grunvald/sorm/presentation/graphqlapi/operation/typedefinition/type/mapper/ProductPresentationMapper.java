package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Product;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {PricePresentationMapper.class, MapperTypeConverter.class})
public interface ProductPresentationMapper {

    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Product map(com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product product);
}