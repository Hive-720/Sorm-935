package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.condition.DateTimeCondition;
import com.kjsoft.sorm.graphql.generated.model.DateTimeStringFilterGql;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperOffsetDateTimeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {DateTimeRangePresentationMapper.class, MapperOffsetDateTimeConverter.class})
public interface DateTimeStringFilterPresentationMapper {

    @Mapping(target = "eq", source = "eq", qualifiedByName = "stringToUtcOffsetDateTime")
    @Mapping(target = "in", source = "in", qualifiedByName = "stringListToUtcOffsetDateTimeList")
    @Mapping(target = "le", source = "le", qualifiedByName = "stringToUtcOffsetDateTime")
    @Mapping(target = "lt", source = "lt", qualifiedByName = "stringToUtcOffsetDateTime")
    @Mapping(target = "ge", source = "ge", qualifiedByName = "stringToUtcOffsetDateTime")
    @Mapping(target = "gt", source = "gt", qualifiedByName = "stringToUtcOffsetDateTime")
    DateTimeCondition map(DateTimeStringFilterGql filter);

}