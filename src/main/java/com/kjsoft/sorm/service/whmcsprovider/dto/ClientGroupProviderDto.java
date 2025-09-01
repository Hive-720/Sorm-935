package com.kjsoft.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class ClientGroupProviderDto {
    private Long id;
    private String groupName;
    private BigDecimal discountPercent;
}