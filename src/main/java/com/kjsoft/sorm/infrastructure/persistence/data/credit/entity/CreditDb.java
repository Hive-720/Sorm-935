package com.kjsoft.sorm.infrastructure.persistence.data.credit.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
public class CreditDb {
    private Long id;
    private Long clientId;
    private Long relId;
    private String description;
    private BigDecimal amount;
    private OffsetDateTime date;
}