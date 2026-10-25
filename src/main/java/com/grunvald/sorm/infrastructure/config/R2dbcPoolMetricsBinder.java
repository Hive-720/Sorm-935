package com.grunvald.sorm.infrastructure.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.r2dbc.pool.PoolMetrics;

import java.util.Optional;


/**
 * Гейджи пула соединений R2DBC. Spring Boot не биндит метрики r2dbc-pool
 * автоматически, а в r2dbc-pool 1.0.x нет готового MeterBinder,
 * поэтому регистрируем их вручную из {@link PoolMetrics}.
 */
public class R2dbcPoolMetricsBinder implements MeterBinder {

    private final Optional<PoolMetrics> poolMetrics;

    public R2dbcPoolMetricsBinder(Optional<PoolMetrics> poolMetrics) {
        this.poolMetrics = poolMetrics;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        poolMetrics.ifPresent(metrics -> {
            Gauge.builder("r2dbc.pool.acquired", metrics, PoolMetrics::acquiredSize).register(registry);
            Gauge.builder("r2dbc.pool.allocated", metrics, PoolMetrics::allocatedSize).register(registry);
            Gauge.builder("r2dbc.pool.idle", metrics, PoolMetrics::idleSize).register(registry);
            Gauge.builder("r2dbc.pool.pending.acquire", metrics, PoolMetrics::pendingAcquireSize).register(registry);
            Gauge.builder("r2dbc.pool.max.allocated", metrics, PoolMetrics::getMaxAllocatedSize).register(registry);
            Gauge.builder("r2dbc.pool.max.pending.acquire", metrics, PoolMetrics::getMaxPendingAcquireSize).register(registry);
        });
    }
}
