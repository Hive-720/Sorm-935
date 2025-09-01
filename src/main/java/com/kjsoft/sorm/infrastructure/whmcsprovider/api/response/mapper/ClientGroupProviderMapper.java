package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetClientGroupsResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientGroupProviderMapper {

    ClientGroupProviderDto map(GetClientGroupsResponse.Groups.Group group);
}