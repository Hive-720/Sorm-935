package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.DateTimeStringFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = DateTimeStringFilterPresentationMapper.class)
public interface DateTimeBaseFilterPresentationMapper {

    DateTimeBaseFilter map(com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter filter);
}