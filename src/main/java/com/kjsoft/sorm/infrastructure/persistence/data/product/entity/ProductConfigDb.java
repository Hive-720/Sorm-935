package com.kjsoft.sorm.infrastructure.persistence.data.product.entity;

import lombok.Data;


@Data
public class ProductConfigDb {
    private Long id;
    private String name;
    private String type;
}