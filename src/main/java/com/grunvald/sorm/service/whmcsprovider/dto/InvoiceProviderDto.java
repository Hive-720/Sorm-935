package com.grunvald.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
public class InvoiceProviderDto {
    private Long id;
    private Long clientId;
    private OffsetDateTime date;
    private OffsetDateTime dueDate;
    private OffsetDateTime datePaid;
    private OffsetDateTime dateRefunded;
    private OffsetDateTime dateCancelled;
    private BigDecimal subTotal;
    private BigDecimal credit;
    private BigDecimal tax;
    private BigDecimal tax2;
    private BigDecimal total;
    private BigDecimal taxRate;
    private BigDecimal taxRate2;
    private String status;
    private String paymentMethod;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String currencyCode;
}
