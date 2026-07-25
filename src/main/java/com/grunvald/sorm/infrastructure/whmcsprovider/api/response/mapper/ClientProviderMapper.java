package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientsDetailsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientProviderMapper {

    ClientProviderDto map(GetClientsDetailsResponse response);
}