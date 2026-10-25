package com.grunvald.sorm.infrastructure.config;

import io.micrometer.core.instrument.binder.MeterBinder;
import io.r2dbc.pool.ConnectionPool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class R2dbcPoolMetricsConfig {

    @Bean
    @ConditionalOnBean(ConnectionPool.class)
    public MeterBinder r2dbcPoolMetrics(ConnectionPool connectionPool) {
        return new R2dbcPoolMetricsBinder(connectionPool.getMetrics());
    }
}
