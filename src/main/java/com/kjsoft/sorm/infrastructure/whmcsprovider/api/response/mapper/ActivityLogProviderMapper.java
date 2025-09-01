package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetActivityLogResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.ActivityLogProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ActivityLogProviderMapper {

    ActivityLogProviderDto map(GetActivityLogResponse.Activity.ActivityLog activityLog);
}