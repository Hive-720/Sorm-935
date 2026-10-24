package com.grunvald.sorm.syncdatascheduler.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.NORMAL;
import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulerPropertiesConfigTest {

    @Test
    void defaults() {
        var config = new TestSchedulerPropertiesConfig();

        assertFalse(config.isEnabled());
        assertEquals(NORMAL, config.getMode());
        assertNull(config.getCronExpression());

        var firstStart = config.getFirstStart();
        assertNotNull(firstStart);
        assertTrue(firstStart.isContinueAfterError());
        assertEquals(List.of(1000, 5000, 10000, 20000, 60000, 120000), firstStart.getDelays());
    }

    @Test
    void setters_overrideDefaults() {
        var config = new TestSchedulerPropertiesConfig();
        var firstStart = new SchedulerPropertiesConfig.FirstStart(List.of(1, 2), false);

        config.setEnabled(true);
        config.setMode(ONLY_FIRST);
        config.setCronExpression("0 0 * * * *");
        config.setFirstStart(firstStart);

        assertTrue(config.isEnabled());
        assertEquals(ONLY_FIRST, config.getMode());
        assertEquals("0 0 * * * *", config.getCronExpression());
        assertEquals(firstStart, config.getFirstStart());
        assertFalse(firstStart.isContinueAfterError());
        assertEquals(List.of(1, 2), firstStart.getDelays());
    }

    private static class TestSchedulerPropertiesConfig extends SchedulerPropertiesConfig {
    }
}
