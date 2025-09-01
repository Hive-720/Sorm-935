package com.kjsoft.sorm.infrastructure.persistence.data.product.entity.relation;

import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class ProductConfigRelationDb {
    private Long productId;
    private Long productConfigId;
    private OffsetDateTime createdAt;
}