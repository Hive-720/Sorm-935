package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.ActivityLogFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ActivityLogFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        ClientFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class,
        UserFilterPresentationMapper.class
})
public interface ActivityLogFilterPresentationMapper {

    ActivityLogFilter map(ActivityLogFilterInput input);
}