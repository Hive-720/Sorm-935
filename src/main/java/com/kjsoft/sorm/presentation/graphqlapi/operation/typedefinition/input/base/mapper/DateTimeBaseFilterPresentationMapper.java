package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.DateTimeStringFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = DateTimeStringFilterPresentationMapper.class)
public interface DateTimeBaseFilterPresentationMapper {

    DateTimeBaseFilter map(com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter filter);
}