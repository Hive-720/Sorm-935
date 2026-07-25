package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfig;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductConfigPresentationMapper {

    ProductConfig map(com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig productConfig);
}