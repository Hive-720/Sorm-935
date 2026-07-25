package com.grunvald.sorm.infrastructure.persistence.data.invoice.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
public class InvoiceDb {
    private Long id;
    private Long clientId;
    private BigDecimal subTotal;
    private BigDecimal total;
    private BigDecimal credit;
    private BigDecimal balance;
    private BigDecimal tax;
    private BigDecimal tax2;
    private BigDecimal taxRate;
    private BigDecimal taxRate2;
    private String status;
    private String currency;
    private String paymentMethod;
    private Json items;
    private OffsetDateTime date;
    private OffsetDateTime dueDate;
    private OffsetDateTime datePaid;
    private OffsetDateTime dateRefunded;
    private OffsetDateTime dateCancelled;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}