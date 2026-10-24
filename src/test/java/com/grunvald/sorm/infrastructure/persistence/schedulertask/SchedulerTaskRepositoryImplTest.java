package com.grunvald.sorm.infrastructure.persistence.schedulertask;

import com.grunvald.sorm.infrastructure.persistence.schedulertask.entity.SchedulerTaskDb;
import com.grunvald.sorm.infrastructure.persistence.schedulertask.entity.mapper.SyncTaskPersistenceMapperImpl;
import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.ACTIVITY_LOG;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.FINISHED;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SchedulerTaskRepositoryImplTest {

    private static final OffsetDateTime START = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final OffsetDateTime FINISH = OffsetDateTime.parse("2026-01-02T10:00:00Z");

    private DatabaseClient databaseClient;
    private SchedulerTaskRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        databaseClient = mock(DatabaseClient.class);
        repository = new SchedulerTaskRepositoryImpl(databaseClient, new SyncTaskPersistenceMapperImpl());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findBySchedulerType_taskFound_returnsMappedTask() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind("schedulerType", ACTIVITY_LOG.name())).thenReturn(spec);
        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SchedulerTaskDb.class)).thenReturn(rows);
        when(rows.one()).thenReturn(Mono.just(dbTask()));

        var result = repository.findBySchedulerType(ACTIVITY_LOG).block();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ACTIVITY_LOG, result.getSchedulerType());
        assertEquals("cur", result.getCursor());
        assertEquals(10L, result.getOffset());
        assertEquals(IN_PROGRESS, result.getStatus());
        assertEquals(START, result.getStartAt());
        assertEquals(FINISH, result.getFinishAt());
        verify(spec).bind("schedulerType", ACTIVITY_LOG.name());
        verify(spec).mapProperties(SchedulerTaskDb.class);
        verify(rows).one();
    }

    @Test
    @SuppressWarnings("unchecked")
    void findBySchedulerType_noTask_returnsEmpty() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind("schedulerType", ACTIVITY_LOG.name())).thenReturn(spec);
        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SchedulerTaskDb.class)).thenReturn(rows);
        when(rows.one()).thenReturn(Mono.empty());

        var result = repository.findBySchedulerType(ACTIVITY_LOG).block();

        assertNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void add_bindsParamsAndReturnsMappedTask() {
        var task = new SchedulerTask(ACTIVITY_LOG, START);
        task.setCursor("cur");
        task.setOffset(5);
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(0, ACTIVITY_LOG.name())).thenReturn(spec);
        when(spec.bind(1, "cur")).thenReturn(spec);
        when(spec.bind(2, 5L)).thenReturn(spec);
        when(spec.bind(3, IN_PROGRESS.name())).thenReturn(spec);
        when(spec.bind(4, START)).thenReturn(spec);
        when(spec.bindNull(5, OffsetDateTime.class)).thenReturn(spec);
        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SchedulerTaskDb.class)).thenReturn(rows);
        when(rows.one()).thenReturn(Mono.just(dbTask()));

        var result = repository.add(task).block();

        assertNotNull(result);
        assertEquals(ACTIVITY_LOG, result.getSchedulerType());
        verify(spec).bind(0, ACTIVITY_LOG.name());
        verify(spec).bind(1, "cur");
        verify(spec).bind(2, 5L);
        verify(spec).bind(3, IN_PROGRESS.name());
        verify(spec).bind(4, START);
        verify(spec).bindNull(5, OffsetDateTime.class);
        verify(spec).mapProperties(SchedulerTaskDb.class);
        verify(rows).one();
    }

    @Test
    @SuppressWarnings("unchecked")
    void update_bindsParamsAndReturnsMappedTask() {
        var task = new SchedulerTask(ACTIVITY_LOG, START);
        task.setId(9L);
        task.setCursor(null);
        task.setOffset(7);
        task.finish(FINISH);
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(0, ACTIVITY_LOG.name())).thenReturn(spec);
        when(spec.bindNull(1, String.class)).thenReturn(spec);
        when(spec.bind(2, 7L)).thenReturn(spec);
        when(spec.bind(3, FINISHED.name())).thenReturn(spec);
        when(spec.bind(4, FINISH)).thenReturn(spec);
        when(spec.bind(5, 9L)).thenReturn(spec);
        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SchedulerTaskDb.class)).thenReturn(rows);
        when(rows.one()).thenReturn(Mono.just(dbTask()));

        var result = repository.update(task).block();

        assertNotNull(result);
        assertEquals(ACTIVITY_LOG, result.getSchedulerType());
        verify(spec).bind(0, ACTIVITY_LOG.name());
        verify(spec).bindNull(1, String.class);
        verify(spec).bind(2, 7L);
        verify(spec).bind(3, FINISHED.name());
        verify(spec).bind(4, FINISH);
        verify(spec).bind(5, 9L);
        verify(spec).mapProperties(SchedulerTaskDb.class);
        verify(rows).one();
    }

    private SchedulerTaskDb dbTask() {
        var db = new SchedulerTaskDb();
        db.setId(1L);
        db.setSchedulerType(ACTIVITY_LOG.name());
        db.setCursor("cur");
        db.setOffset(10);
        db.setStatus(IN_PROGRESS.name());
        db.setStartAt(START);
        db.setFinishAt(FINISH);
        return db;
    }
}
