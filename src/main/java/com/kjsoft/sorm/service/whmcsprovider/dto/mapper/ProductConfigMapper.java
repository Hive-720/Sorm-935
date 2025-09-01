package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.kjsoft.sorm.service.whmcsprovider.dto.ProductProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductConfigMapper {

    ProductConfig map(ProductProviderDto.Config config);
}