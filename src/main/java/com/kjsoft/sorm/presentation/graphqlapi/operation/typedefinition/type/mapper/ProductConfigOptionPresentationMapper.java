package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfigOption;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = PricePresentationMapper.class)
public interface ProductConfigOptionPresentationMapper {

    ProductConfigOption map(com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption productConfigOption);
}