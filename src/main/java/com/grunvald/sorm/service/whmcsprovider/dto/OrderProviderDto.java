package com.grunvald.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
public class OrderProviderDto {
    private Long id;
    private Long clientId;
    private Long contactId;
    private OffsetDateTime date;
    private BigDecimal amount;
    private String paymentMethod;
    private Long invoiceId;
    private String status;
    private String ipAddress;
    private String paymentMethodName;
    private String paymentStatus;
    private String currency;
    private List<LineItem> lineItems;


    @Getter
    @Setter
    public static class LineItem {
        private String type;
        private Long relId;
        private String productType;
        private String product;
        private String domain;
        private BigDecimal billingCycle;
        private BigDecimal amount;
        private String status;
    }
}