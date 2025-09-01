package com.kjsoft.sorm.infrastructure.persistence.data.product.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class ProductDb {
    private Long id;
    private String type;
    private String name;
    private String description;
    private String payType;
    private Json prices;
    private OffsetDateTime updatedAt;
    private boolean deleted;
}