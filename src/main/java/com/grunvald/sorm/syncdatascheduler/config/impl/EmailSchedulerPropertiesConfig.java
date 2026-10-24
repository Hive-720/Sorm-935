package com.grunvald.sorm.syncdatascheduler.config.impl;

import com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.SyncConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "sorm.whmcs.data-sync.schedulers.email")
@ConditionalOnBean(SyncConfig.class)
@ConditionalOnProperty(name = "sorm.whmcs.data_sync.schedulers.email.enabled",
        havingValue = "true", matchIfMissing = true)
public class EmailSchedulerPropertiesConfig extends SchedulerPropertiesConfig {
    @Positive
    private Integer clientLimit = 15;
    @PositiveOrZero
    private Integer maxAgeYears = 3;
    @Positive
    private Integer maxClientEmailsPerTask = 1000;
    @Positive
    private Integer limit = 25;
    private boolean skipBatchError = false;
    private List<@NotBlank String> maskBodyMessagePatterns = List.of();
}