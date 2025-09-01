package com.kjsoft.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
public class CreditProviderDto {
    private Long id;
    private OffsetDateTime date;
    private String description;
    private BigDecimal amount;
    private Long relId;
}