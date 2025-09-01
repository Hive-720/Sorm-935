package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.product.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.product.ProductPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductFilterPersistenceMapper {

    ProductPersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.ProductFilter filter);
}