package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.condition.StringTermExactCondition;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperListConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface StringTermExactFilterPresentationMapper extends MapperListConverter {

    @Mapping(source = "allofterms", target = "allOfTerms", qualifiedByName = "commaSeparatedToList")
    @Mapping(source = "anyofterms", target = "anyOfTerms", qualifiedByName = "commaSeparatedToList")
    StringTermExactCondition map(StringTermExactFilter filter);
}