package com.grunvald.sorm.infrastructure.persistence.data.order.entity;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class OrderItemDb {
    private Long id;
    private Long orderId;
    private Long relId;
    private String type;
    private String productType;
    private String productName;
    private String domain;
    private BigDecimal billingCycle;
    private BigDecimal amount;
    private String status;
}