package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.condition.StringTermFullTextCondition;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermFullTextFilter;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperListConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface StringTermFullTextFilterPresentationMapper extends MapperListConverter {

    @Mapping(source = "alloftext", target = "allOfText")
    @Mapping(source = "anyoftext", target = "anyOfText")
    @Mapping(source = "allofterms", target = "allOfTerms", qualifiedByName = "commaSeparatedToList")
    @Mapping(source = "anyofterms", target = "anyOfTerms", qualifiedByName = "commaSeparatedToList")
    StringTermFullTextCondition map(StringTermFullTextFilter filter);
}