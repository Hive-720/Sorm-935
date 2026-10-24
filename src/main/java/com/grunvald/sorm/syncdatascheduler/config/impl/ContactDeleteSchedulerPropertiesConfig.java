package com.grunvald.sorm.syncdatascheduler.config.impl;

import com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.SyncConfig;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.NORMAL;


@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "sorm.whmcs.data-sync.schedulers.contact-delete")
@ConditionalOnBean(SyncConfig.class)
@ConditionalOnProperty(name = "sorm.whmcs.data_sync.schedulers.contact_delete.enabled",
        havingValue = "true", matchIfMissing = true)
public class ContactDeleteSchedulerPropertiesConfig extends SchedulerPropertiesConfig {
    @Positive
    private Integer limit = 25;


    @Override
    public Mode getMode() {
        return NORMAL;
    }

    @Override
    public FirstStart getFirstStart() {
        return null;
    }
}