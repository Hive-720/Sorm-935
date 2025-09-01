package com.kjsoft.sorm.syncdatascheduler.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "sorm.whmcs.data_sync")
@ConditionalOnProperty(name = "sorm.whmcs.data_sync.enabled", havingValue = "true")
public class SyncConfig {
    private boolean enabled = false;
}