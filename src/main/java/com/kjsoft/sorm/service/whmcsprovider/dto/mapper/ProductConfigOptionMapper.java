package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.kjsoft.sorm.service.whmcsprovider.dto.ProductProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductConfigOptionMapper {

    ProductConfigOption map(ProductProviderDto.Config.Option option, Long productConfigId);

}