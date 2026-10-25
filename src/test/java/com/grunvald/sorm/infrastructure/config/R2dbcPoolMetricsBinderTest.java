package com.grunvald.sorm.infrastructure.config;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.r2dbc.pool.PoolMetrics;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class R2dbcPoolMetricsBinderTest {

    @Test
    void bindTo_registersAllPoolGauges() {
        var registry = new SimpleMeterRegistry();
        new R2dbcPoolMetricsBinder(Optional.of(poolMetrics(7, 5, 2, 1, 30, 10))).bindTo(registry);

        assertEquals(7.0, registry.get("r2dbc.pool.acquired").gauge().value());
        assertEquals(5.0, registry.get("r2dbc.pool.allocated").gauge().value());
        assertEquals(2.0, registry.get("r2dbc.pool.idle").gauge().value());
        assertEquals(1.0, registry.get("r2dbc.pool.pending.acquire").gauge().value());
        assertEquals(30.0, registry.get("r2dbc.pool.max.allocated").gauge().value());
        assertEquals(10.0, registry.get("r2dbc.pool.max.pending.acquire").gauge().value());
    }

    @Test
    void bindTo_emptyMetrics_registersNothing() {
        var registry = new SimpleMeterRegistry();
        new R2dbcPoolMetricsBinder(Optional.empty()).bindTo(registry);

        assertEquals(0, registry.getMeters().size());
    }

    private static PoolMetrics poolMetrics(int acquired, int allocated, int idle, int pendingAcquire,
                                           int maxAllocated, int maxPendingAcquire) {
        return new PoolMetrics() {
            @Override
            public int acquiredSize() {
                return acquired;
            }

            @Override
            public int allocatedSize() {
                return allocated;
            }

            @Override
            public int idleSize() {
                return idle;
            }

            @Override
            public int pendingAcquireSize() {
                return pendingAcquire;
            }

            @Override
            public int getMaxAllocatedSize() {
                return maxAllocated;
            }

            @Override
            public int getMaxPendingAcquireSize() {
                return maxPendingAcquire;
            }
        };
    }
}
