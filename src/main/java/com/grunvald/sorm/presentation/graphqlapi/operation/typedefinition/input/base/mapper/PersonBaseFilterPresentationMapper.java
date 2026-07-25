package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.base.PersonBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermExactFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermFullTextFilterPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {
        StringTermFullTextFilterPresentationMapper.class,
        StringTermExactFilterPresentationMapper.class
})
public interface PersonBaseFilterPresentationMapper {

    @Mapping(target = "firstName", source = "name")
    PersonBaseFilter map(com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.PersonBaseFilter filter);
}