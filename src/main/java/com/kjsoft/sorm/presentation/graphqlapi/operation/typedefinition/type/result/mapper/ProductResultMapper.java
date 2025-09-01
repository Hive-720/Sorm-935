package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.dto.ProductSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ProductResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductPresentationMapper.class)
public interface ProductResultMapper {

    @Mapping(target = "result", source = "productList")
    ProductResult map(ProductSearchResult result);
}