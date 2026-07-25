package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetUsersResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.UserProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserProviderMapper {

    UserProviderDto map(GetUsersResponse.User user);
}