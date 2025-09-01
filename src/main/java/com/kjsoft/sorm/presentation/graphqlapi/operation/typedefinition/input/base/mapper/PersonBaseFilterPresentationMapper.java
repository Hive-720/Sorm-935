package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.base.PersonBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermExactFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermFullTextFilterPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {
        StringTermFullTextFilterPresentationMapper.class,
        StringTermExactFilterPresentationMapper.class
})
public interface PersonBaseFilterPresentationMapper {

    @Mapping(target = "firstName", source = "name")
    PersonBaseFilter map(com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.PersonBaseFilter filter);
}