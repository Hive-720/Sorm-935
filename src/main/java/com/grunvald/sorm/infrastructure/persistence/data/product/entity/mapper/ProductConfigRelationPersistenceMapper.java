package com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.product.entity.relation.ProductConfigRelationDb;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductConfigRelationPersistenceMapper {

    ProductConfigRelation map(ProductConfigRelationDb relationDb);

    ProductConfigRelationDb map(ProductConfigRelation relation);
}