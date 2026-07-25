package com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
public class AffiliateDb {
    private Long id;
    private Long clientId;
    private Long visitors;
    private String payType;
    private BigDecimal payAmount;
    private Boolean oneTime;
    private BigDecimal balance;
    private BigDecimal withdrawn;
    private OffsetDateTime date;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}