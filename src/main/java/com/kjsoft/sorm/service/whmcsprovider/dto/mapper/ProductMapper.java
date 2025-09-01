package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.kjsoft.sorm.service.whmcsprovider.dto.ProductProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Product map(ProductProviderDto dto);
}