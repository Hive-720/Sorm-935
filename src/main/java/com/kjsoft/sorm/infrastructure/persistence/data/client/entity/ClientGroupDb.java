package com.kjsoft.sorm.infrastructure.persistence.data.client.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
public class ClientGroupDb {
    private Long id;
    private String name;
    private BigDecimal discountPercent;
    private OffsetDateTime updatedAt;
    private boolean deleted;
}