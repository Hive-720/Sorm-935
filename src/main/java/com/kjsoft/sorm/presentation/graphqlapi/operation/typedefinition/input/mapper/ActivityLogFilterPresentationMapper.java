package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.ActivityLogFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ActivityLogFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        ClientFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class,
        UserFilterPresentationMapper.class
})
public interface ActivityLogFilterPresentationMapper {

    ActivityLogFilter map(ActivityLogFilterInput input);
}