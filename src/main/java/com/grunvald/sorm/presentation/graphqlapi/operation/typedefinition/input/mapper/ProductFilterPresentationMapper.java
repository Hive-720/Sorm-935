package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.ProductFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ProductFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = DateTimeBaseFilterPresentationMapper.class)
public interface ProductFilterPresentationMapper {

    ProductFilter map(ProductFilterInput input);
}