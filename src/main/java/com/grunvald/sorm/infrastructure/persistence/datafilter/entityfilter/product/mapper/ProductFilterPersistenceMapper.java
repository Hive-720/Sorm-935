package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.product.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.product.ProductPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductFilterPersistenceMapper {

    ProductPersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.ProductFilter filter);
}