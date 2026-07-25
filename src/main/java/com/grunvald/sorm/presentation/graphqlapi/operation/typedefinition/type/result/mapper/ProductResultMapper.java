package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.dto.ProductSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ProductResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductPresentationMapper.class)
public interface ProductResultMapper {

    @Mapping(target = "result", source = "productList")
    ProductResult map(ProductSearchResult result);
}