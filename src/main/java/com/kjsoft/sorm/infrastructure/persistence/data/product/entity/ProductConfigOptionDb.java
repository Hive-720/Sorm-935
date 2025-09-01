package com.kjsoft.sorm.infrastructure.persistence.data.product.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.Data;


@Data
public class ProductConfigOptionDb {
    private Long id;
    private Long productConfigId;
    private String name;
    private Json prices;
}