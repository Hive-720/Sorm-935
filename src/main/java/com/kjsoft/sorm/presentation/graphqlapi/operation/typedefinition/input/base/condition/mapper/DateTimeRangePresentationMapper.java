package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.condition.DateTimeRangeCondition;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.DateTimeRange;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperOffsetDateTimeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperOffsetDateTimeConverter.class)
public interface DateTimeRangePresentationMapper {

    @Mapping(target = "min", source = "min", qualifiedByName = "stringToUtcOffsetDateTime")
    @Mapping(target = "max", source = "max", qualifiedByName = "stringToUtcOffsetDateTime")
    DateTimeRangeCondition map(DateTimeRange dateTimeRange);
}