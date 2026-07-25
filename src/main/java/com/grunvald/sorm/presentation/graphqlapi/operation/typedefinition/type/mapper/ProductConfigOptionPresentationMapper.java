package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfigOption;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = PricePresentationMapper.class)
public interface ProductConfigOptionPresentationMapper {

    ProductConfigOption map(com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption productConfigOption);
}