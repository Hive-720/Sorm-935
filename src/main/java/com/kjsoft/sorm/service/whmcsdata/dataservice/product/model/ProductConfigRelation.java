package com.kjsoft.sorm.service.whmcsdata.dataservice.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;


@Getter
@AllArgsConstructor
public class ProductConfigRelation {
    private Long productId;
    private Long productConfigId;
    private OffsetDateTime createdAt;
}