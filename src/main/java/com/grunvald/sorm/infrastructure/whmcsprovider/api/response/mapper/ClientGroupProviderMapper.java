package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientGroupsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientGroupProviderMapper {

    ClientGroupProviderDto map(GetClientGroupsResponse.Groups.Group group);
}