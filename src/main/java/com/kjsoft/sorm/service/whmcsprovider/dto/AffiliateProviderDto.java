package com.kjsoft.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
public class AffiliateProviderDto {
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