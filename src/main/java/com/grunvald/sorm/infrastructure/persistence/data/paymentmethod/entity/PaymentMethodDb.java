package com.grunvald.sorm.infrastructure.persistence.data.paymentmethod.entity;

import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class PaymentMethodDb {
    private Long id;
    private String module;
    private String displayName;
    private OffsetDateTime updatedAt;
    private boolean deleted;
}