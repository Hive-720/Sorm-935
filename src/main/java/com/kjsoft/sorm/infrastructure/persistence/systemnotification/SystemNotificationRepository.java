package com.kjsoft.sorm.infrastructure.persistence.systemnotification;

import com.kjsoft.sorm.infrastructure.persistence.systemnotification.entity.SystemNotificationDB;
import com.kjsoft.sorm.infrastructure.persistence.util.PersistenceHelper;
import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import com.kjsoft.sorm.infrastructure.persistence.systemnotification.entity.mapper.SystemNotificationPersistenceMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;


@Slf4j
@Repository
@AllArgsConstructor
public class SystemNotificationRepository {
    private final DatabaseClient databaseClient;
    private final SystemNotificationPersistenceMapper mapper;


    public Mono<SystemNotification> add(SystemNotification notify) {
        var notifyDb = mapper.map(notify);
        var spec = databaseClient.sql("""
                INSERT INTO system_notifications (type, message, sent, created_at, metric_alerts)
                VALUES ($1, $2, $3, $4, $5)
                RETURNING id, type, message, sent, created_at, metric_alerts
                """);

        spec = PersistenceHelper.bindOrNull(spec, 0, notifyDb.getType(), String.class);
        spec = PersistenceHelper.bindOrNull(spec, 1, notifyDb.getMessage(), String.class);
        spec = PersistenceHelper.bindOrNull(spec, 2, notifyDb.isSent(), Boolean.class);
        spec = PersistenceHelper.bindOrNull(spec, 3, notifyDb.getCreatedAt(), OffsetDateTime.class);
        spec = PersistenceHelper.bindOrNull(spec, 4, notifyDb.getMetricAlerts(), Json.class);

        return spec.mapProperties(SystemNotificationDB.class)
                .one()
                .map(mapper::map);
    }

    public Mono<SystemNotification> update(SystemNotification notify) {
        var notifyDb = mapper.map(notify);
        var spec = databaseClient.sql("""
                 UPDATE system_notifications
                 SET type = $1,
                     message = $2,
                     sent = $3,
                     metric_alerts = $4
                 WHERE id = $5
                 RETURNING id, type, message, sent, created_at, metric_alerts
                """);

        spec = PersistenceHelper.bindOrNull(spec, 0, notifyDb.getType(), String.class);
        spec = PersistenceHelper.bindOrNull(spec, 1, notifyDb.getMessage(), String.class);
        spec = PersistenceHelper.bindOrNull(spec, 2, notifyDb.isSent(), Boolean.class);
        spec = PersistenceHelper.bindOrNull(spec, 3, notifyDb.getMetricAlerts(), Json.class);
        spec = spec.bind(4, notifyDb.getId());

        return spec
                .mapProperties(SystemNotificationDB.class)
                .one()
                .map(mapper::map);
    }

    public Flux<SystemNotification> findAllBySentIsFalse() {
        return databaseClient.sql("SELECT * FROM system_notifications WHERE sent = false")
                .mapProperties(SystemNotificationDB.class)
                .all()
                .map(mapper::map);
    }
}