package com.grunvald.sorm.infrastructure.persistence.data.client.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
public class ClientProductDb {
    private Long id;
    private Long clientId;
    private Long productId;
    private Long orderId;
    private String name;
    private String domain;
    private String dedicatedIpAddress;
    private BigDecimal firstPaymentAmount;
    private BigDecimal recurringAmount;
    private String billingCycle;
    private String[] assignedIps;
    private String status;
    private Json configs;
    private OffsetDateTime regDate;
    private OffsetDateTime nextDueDate;
    private OffsetDateTime lastUpdate;
}