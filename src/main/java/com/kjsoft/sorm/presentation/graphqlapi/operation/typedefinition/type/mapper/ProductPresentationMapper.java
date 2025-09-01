package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Product;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {PricePresentationMapper.class, MapperTypeConverter.class})
public interface ProductPresentationMapper {

    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Product map(com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.Product product);
}