package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.condition.StringRegExpFullTextCondition;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpFullTextFilter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface StringRegExpFullTextFilterPresentationMapper {

    @Mapping(target = "allOfText", source = "alloftext")
    @Mapping(target = "anyOfText", source = "anyoftext")
    StringRegExpFullTextCondition map(StringRegExpFullTextFilter address);
}
