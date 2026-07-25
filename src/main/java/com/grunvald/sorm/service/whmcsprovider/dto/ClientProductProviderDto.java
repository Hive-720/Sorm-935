package com.grunvald.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
public class ClientProductProviderDto {
    private Long id;
    private Long clientId;
    private Long orderId;
    private Long pid;
    private OffsetDateTime regDate;
    private String name;
    private String domain;
    private String dedicatedIp;
    private BigDecimal firstPaymentAmount;
    private BigDecimal recurringAmount;
    private String billingCycle;
    private OffsetDateTime nextDueDate;
    private String status;
    private List<String> assignedIps;
    private OffsetDateTime lastUpdate;
    private List<ConfigOption> configOptions;

    @Getter
    @Setter
    public static class ConfigOption {
        private Long id;
        private String option;
        private String type;
        private String value;
    }
}