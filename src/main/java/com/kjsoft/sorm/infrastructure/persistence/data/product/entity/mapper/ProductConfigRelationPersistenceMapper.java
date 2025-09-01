package com.kjsoft.sorm.infrastructure.persistence.data.product.entity.mapper;

import com.kjsoft.sorm.infrastructure.persistence.data.product.entity.relation.ProductConfigRelationDb;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductConfigRelationPersistenceMapper {

    ProductConfigRelation map(ProductConfigRelationDb relationDb);

    ProductConfigRelationDb map(ProductConfigRelation relation);
}