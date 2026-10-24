package com.grunvald.sorm.infrastructure.persistence.schedulertask.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.schedulertask.entity.SchedulerTaskDb;
import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.ACTIVITY_LOG;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.FINISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SyncTaskPersistenceMapperTest {

    private static final OffsetDateTime START = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final OffsetDateTime FINISH = OffsetDateTime.parse("2026-01-02T10:00:00Z");

    private SyncTaskPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SyncTaskPersistenceMapperImpl();
    }

    @Test
    void shouldMapDbToModel() {
        var db = new SchedulerTaskDb();
        db.setId(1L);
        db.setSchedulerType(ACTIVITY_LOG.name());
        db.setCursor("cur");
        db.setOffset(10);
        db.setStatus(FINISHED.name());
        db.setStartAt(START);
        db.setFinishAt(FINISH);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(ACTIVITY_LOG, result.getSchedulerType());
        assertEquals("cur", result.getCursor());
        assertEquals(10L, result.getOffset());
        assertEquals(FINISHED, result.getStatus());
        assertEquals(START, result.getStartAt());
        assertEquals(FINISH, result.getFinishAt());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new SchedulerTask(ACTIVITY_LOG, START);
        model.setId(1L);
        model.setCursor("cur");
        model.setOffset(10);
        model.finish(FINISH);

        var result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(ACTIVITY_LOG.name(), result.getSchedulerType());
        assertEquals("cur", result.getCursor());
        assertEquals(10L, result.getOffset());
        assertEquals(FINISHED.name(), result.getStatus());
        assertEquals(START, result.getStartAt());
        assertEquals(FINISH, result.getFinishAt());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((SchedulerTaskDb) null));
        assertNull(mapper.map((SchedulerTask) null));
    }
}
