package com.kjsoft.sorm.syncdatascheduler.config.impl;

import com.kjsoft.sorm.syncdatascheduler.config.SchedulerPropertiesConfig;
import com.kjsoft.sorm.syncdatascheduler.config.SyncConfig;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "sorm.whmcs.data-sync.credit")
@ConditionalOnBean(SyncConfig.class)
@ConditionalOnProperty(name = "sorm.whmcs.data_sync.schedulers.credit.enabled",
        havingValue = "true", matchIfMissing = true)
public class CreditSchedulerPropertiesConfig extends SchedulerPropertiesConfig {
    @Positive
    private Integer clientLimit = 25;
    @PositiveOrZero
    private Integer maxAgeYears = 3;
    @Positive
    private Integer maxClientCreditPerTask = 1000;
    private boolean skipBatchError = false;
}