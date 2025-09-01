package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetClientsDetailsResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.ClientProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientProviderMapper {

    ClientProviderDto map(GetClientsDetailsResponse response);
}