package com.grunvald.sorm.syncdatascheduler.task.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.FINISHED;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulerTaskTest {

    private static final OffsetDateTime NOW = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    @Test
    void newTask_isInProgressWithDefaults() {
        var task = new SchedulerTask(CLIENT, NOW);

        assertEquals(CLIENT, task.getSchedulerType());
        assertEquals(NOW, task.getStartAt());
        assertEquals(IN_PROGRESS, task.getStatus());
        assertTrue(task.isNotFinished());
        assertFalse(task.isFinished());
        assertEquals(0L, task.getOffset());
        assertNull(task.getCursor());
        assertNull(task.getFinishAt());
        assertNull(task.getId());
    }

    @Test
    void finish_setsFinishedStatusAndTime() {
        var task = new SchedulerTask(CLIENT, NOW);

        task.finish(NOW.plusHours(1));

        assertEquals(FINISHED, task.getStatus());
        assertEquals(NOW.plusHours(1), task.getFinishAt());
        assertTrue(task.isFinished());
        assertFalse(task.isNotFinished());
    }

    @Test
    void setters_updateCursorOffsetAndId() {
        var task = new SchedulerTask(CLIENT, NOW);

        task.setCursor("c1");
        task.setOffset(42);
        task.setId(7L);

        assertEquals("c1", task.getCursor());
        assertEquals(42L, task.getOffset());
        assertEquals(7L, task.getId());
    }

    @Test
    void empty_hasNoTypeAndIsNotFinished() {
        assertNull(SchedulerTask.EMPTY.getSchedulerType());
        assertNull(SchedulerTask.EMPTY.getStartAt());
        assertTrue(SchedulerTask.EMPTY.isNotFinished());
        assertEquals(0L, SchedulerTask.EMPTY.getOffset());
        assertNull(SchedulerTask.EMPTY.getCursor());
    }
}
