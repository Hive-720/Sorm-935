package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.base.AddressBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringRegExpFullTextFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermExactFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        StringTermExactFilterPresentationMapper.class,
        StringRegExpFullTextFilterPresentationMapper.class
})
public interface AddressBaseFilterPresentationMapper {

    AddressBaseFilter map(com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.AddressBaseFilter filter);
}
