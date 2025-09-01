package com.kjsoft.sorm.service.whmcsdata.dataservice.credit.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
public class Credit {
    private Long id;
    private Long clientId;
    private Long relId;
    private String description;
    private BigDecimal amount;
    private OffsetDateTime date;
}