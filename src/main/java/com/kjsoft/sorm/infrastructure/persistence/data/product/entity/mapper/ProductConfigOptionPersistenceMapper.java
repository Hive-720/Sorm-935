package com.kjsoft.sorm.infrastructure.persistence.data.product.entity.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.kjsoft.sorm.infrastructure.persistence.data.product.entity.ProductConfigOptionDb;
import com.kjsoft.sorm.infrastructure.persistence.data.product.entity.mapper.converter.PriceMapperConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public abstract class ProductConfigOptionPersistenceMapper extends PriceMapperConverter {

    @Mapping(target = "prices", source = "prices", qualifiedByName = "jsonToPriceList")
    public abstract ProductConfigOption map(ProductConfigOptionDb productConfigOptionDb);

    @Mapping(target = "prices", source = "prices", qualifiedByName = "priceListToJson")
    public abstract ProductConfigOptionDb map(ProductConfigOption productConfigOptionDb);
}