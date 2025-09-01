package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.ProductFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ProductFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = DateTimeBaseFilterPresentationMapper.class)
public interface ProductFilterPresentationMapper {

    ProductFilter map(ProductFilterInput input);
}