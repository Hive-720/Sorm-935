package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetUsersResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.UserProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserProviderMapper {

    UserProviderDto map(GetUsersResponse.User user);
}