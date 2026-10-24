package com.grunvald.sorm.presentation.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestApiConfigTest {

    @Test
    void getApiConfig_nullMap_returnsDefault() {
        var config = new RestApiConfig();
        config.setConfigs(null);

        var apiConfig = config.getApiConfig(RestApiConfig.Api.PUSH);

        assertEquals(50, apiConfig.getMaxRequests());
    }

    @Test
    void getApiConfig_missingKey_returnsDefault() {
        var config = new RestApiConfig();
        config.setConfigs(new java.util.EnumMap<>(RestApiConfig.Api.class));

        assertEquals(50, config.getApiConfig(RestApiConfig.Api.PUSH).getMaxRequests());
    }

    @Test
    void getApiConfig_configuredValue_used() {
        var config = new RestApiConfig();
        var configs = new java.util.EnumMap<RestApiConfig.Api, RestApiConfig.ApiConfig>(RestApiConfig.Api.class);
        configs.put(RestApiConfig.Api.PUSH, new RestApiConfig.ApiConfig(100));
        config.setConfigs(configs);

        assertEquals(100, config.getApiConfig(RestApiConfig.Api.PUSH).getMaxRequests());
        assertEquals(50, config.getApiConfig(RestApiConfig.Api.ALERT).getMaxRequests());
    }

    @Test
    void defaultConfig_isSharedSingletonInstance() {
        // getApiConfig возвращает сам DEFAULT, а не копию
        var config = new RestApiConfig();
        config.setConfigs(new java.util.EnumMap<>(RestApiConfig.Api.class));

        assertEquals(RestApiConfig.ApiConfig.DEFAULT, config.getApiConfig(RestApiConfig.Api.ALERT));
    }
}
