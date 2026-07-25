package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.dto.ActivityLogSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ActivityLogResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ActivityLogPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = ActivityLogPresentationMapper.class)
public interface ActivityLogResultMapper {

    @Mapping(target = "result", source = "activityLogList")
    ActivityLogResult map(ActivityLogSearchResult result);
}