package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientShortProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientShortProviderMapper {

    ClientShortProviderDto map(GetClientsResponse.Clients.Client client);
}