package com.kjsoft.sorm.presentation.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "sorm.rest.api")
public class RestApiConfig {
    private Map<Api, ApiConfig> configs;


    public ApiConfig getApiConfig(Api api) {
        return configs != null ? configs.getOrDefault(api, ApiConfig.DEFAULT) : ApiConfig.DEFAULT;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ApiConfig {
        private Integer maxRequests;

        public static ApiConfig DEFAULT = new ApiConfig(50);
    }

    public enum Api {
        PUSH,
        ALERT
    }
}