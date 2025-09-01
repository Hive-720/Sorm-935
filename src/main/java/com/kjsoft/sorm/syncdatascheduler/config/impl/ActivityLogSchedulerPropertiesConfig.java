package com.kjsoft.sorm.syncdatascheduler.config.impl;

import com.kjsoft.sorm.syncdatascheduler.config.SchedulerPropertiesConfig;
import com.kjsoft.sorm.syncdatascheduler.config.SyncConfig;
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
@ConfigurationProperties(prefix = "sorm.whmcs.data_sync.schedulers.activity_log")
@ConditionalOnBean(SyncConfig.class)
@ConditionalOnProperty(name = "sorm.whmcs.data_sync.schedulers.activity_log.enabled",
        havingValue = "true", matchIfMissing = true)
public class ActivityLogSchedulerPropertiesConfig extends SchedulerPropertiesConfig {
    @Positive
    private Integer limit = 100;
    private boolean skipBatchError = false; //TODO проработать во всех scheduler!!!!
    @PositiveOrZero
    private Integer maxAgeYears = 3;
    private List<@NotBlank String> forbiddenDescriptionPatterns;
}