package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetClientsResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.ClientShortProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientShortProviderMapper {

    ClientShortProviderDto map(GetClientsResponse.Clients.Client client);
}