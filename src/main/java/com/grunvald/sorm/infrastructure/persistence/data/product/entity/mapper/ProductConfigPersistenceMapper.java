package com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductConfigDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductConfigPersistenceMapper {

    ProductConfig map(ProductConfigDb productConfig);

    ProductConfigDb map(ProductConfig productConfig);
}