package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfig;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductConfigPresentationMapper {

    ProductConfig map(com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfig productConfig);
}