package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientProductsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProductProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientProductProviderMapper {

    ClientProductProviderDto map(GetClientProductsResponse.Products.Product product);
}