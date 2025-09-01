package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetClientProductsResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.ClientProductProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientProductProviderMapper {

    ClientProductProviderDto map(GetClientProductsResponse.Products.Product product);
}