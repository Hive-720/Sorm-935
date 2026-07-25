package com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductDb;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.converter.PriceMapperConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public abstract class ProductPersistenceMapper extends PriceMapperConverter {

    @Mapping(target = "prices", source = "prices", qualifiedByName = "jsonToPriceList")
    public abstract Product map(ProductDb product);

    @Mapping(target = "prices", source = "prices", qualifiedByName = "priceListToJson")
    public abstract ProductDb map(Product product);
}