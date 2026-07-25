package com.grunvald.sorm.infrastructure.persistence.data.transaction.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
public class TransactionDb {
    private Long id;
    private Long invoiceId;
    private Long clientId;
    private String transId;
    private Long refundId;
    private String currency;
    private String gateway;
    private String description;
    private BigDecimal amountIn;
    private BigDecimal amountOut;
    private BigDecimal fees;
    private BigDecimal rate;
    private OffsetDateTime date;
}