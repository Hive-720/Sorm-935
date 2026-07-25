package com.grunvald.sorm.infrastructure.persistence.data.order.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
public class OrderDb {
    private Long id;
    private Long clientId;
    private Long contactId;
    private Long invoiceId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String status;
    private String ipAddress;
    private String paymentMethodName;
    private String paymentStatus;
    private OffsetDateTime date;
}