package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.condition.DateTimeRangeCondition;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.DateTimeRange;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperOffsetDateTimeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperOffsetDateTimeConverter.class)
public interface DateTimeRangePresentationMapper {

    @Mapping(target = "min", source = "min", qualifiedByName = "stringToUtcOffsetDateTime")
    @Mapping(target = "max", source = "max", qualifiedByName = "stringToUtcOffsetDateTime")
    DateTimeRangeCondition map(DateTimeRange dateTimeRange);
}