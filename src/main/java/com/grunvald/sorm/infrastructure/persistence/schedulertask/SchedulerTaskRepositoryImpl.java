package com.grunvald.sorm.infrastructure.persistence.schedulertask;

import com.grunvald.sorm.infrastructure.persistence.schedulertask.entity.SchedulerTaskDb;
import com.grunvald.sorm.infrastructure.persistence.schedulertask.entity.mapper.SyncTaskPersistenceMapper;
import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static com.grunvald.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@AllArgsConstructor
public class SchedulerTaskRepositoryImpl implements SchedulerTaskRepository {
    private final DatabaseClient databaseClient;
    private final SyncTaskPersistenceMapper mapper;


    @Override
    public Mono<SchedulerTask> findBySchedulerType(SchedulerType schedulerType) {
        return databaseClient.sql("""
                        SELECT id, scheduler_type, cursor, offset, status, start_at, finish_at
                        FROM sync_tasks
                        WHERE scheduler_type = :schedulerType
                        ORDER BY start_at DESC
                        LIMIT 1
                        """)
                .bind("schedulerType", schedulerType.name())
                .mapProperties(SchedulerTaskDb.class)
                .one()
                .map(mapper::map);
    }

    @Override
    public Mono<SchedulerTask> add(SchedulerTask task) {
        var taskDb = mapper.map(task);
        var spec = databaseClient.sql("""
                INSERT INTO sync_tasks (scheduler_type, cursor, offset, status, start_at, finish_at)
                VALUES ($1, $2, $3, $4, $5, $6)
                RETURNING id, scheduler_type, cursor, offset, status, start_at, finish_at
                """);

        spec = spec.bind(0, taskDb.getSchedulerType());
        spec = bindOrNull(spec, 1, taskDb.getCursor(), String.class);
        spec = bindOrNull(spec, 2, taskDb.getOffset(), Long.class);
        spec = spec.bind(3, taskDb.getStatus());
        spec = spec.bind(4, taskDb.getStartAt());
        spec = spec.bind(5, taskDb.getFinishAt());

        return spec
                .mapProperties(SchedulerTaskDb.class)
                .one()
                .map(mapper::map);
    }

    @Override
    public Mono<SchedulerTask> update(SchedulerTask task) {
        var taskDb = mapper.map(task);

        var spec = databaseClient.sql("""
                 UPDATE sync_tasks
                 SET scheduler_type = $1,
                     cursor = $2,
                     offset = $3,
                     status = $4,
                     finish_at = $5
                 WHERE id = $6
                 RETURNING id, scheduler_type, cursor, offset, status, start_at, finish_at
                """);

        spec = spec.bind(0, taskDb.getSchedulerType());
        spec = bindOrNull(spec, 1, taskDb.getCursor(), String.class);
        spec = bindOrNull(spec, 2, taskDb.getOffset(), Long.class);
        spec = spec.bind(3, taskDb.getStatus());
        spec = bindOrNull(spec, 4, taskDb.getFinishAt(), OffsetDateTime.class);
        spec = spec.bind(5, taskDb.getId());

        return spec
                .mapProperties(SchedulerTaskDb.class)
                .one()
                .map(mapper::map);
    }
}