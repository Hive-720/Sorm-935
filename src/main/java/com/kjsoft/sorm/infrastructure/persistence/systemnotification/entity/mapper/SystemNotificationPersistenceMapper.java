package com.kjsoft.sorm.infrastructure.persistence.systemnotification.entity.mapper;

import com.kjsoft.sorm.service.exception.InternalErrorException;
import com.kjsoft.sorm.service.system.notification.model.MetricAlerts;
import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import com.kjsoft.sorm.infrastructure.persistence.systemnotification.entity.SystemNotificationDB;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class SystemNotificationPersistenceMapper {

    @Autowired
    protected ObjectMapper objectMapper;


    @Mapping(target = "metricAlerts", source = "metricAlerts", qualifiedByName = "metricAlertsToJson")
    public abstract SystemNotificationDB map(SystemNotification systemNotification);

    @Mapping(target = "metricAlerts", source = "metricAlerts", qualifiedByName = "jsonToMetricAlerts")
    public abstract SystemNotification map(SystemNotificationDB systemNotificationDB);

    @Named("metricAlertsToJson")
    protected Json metricAlertsToJson(MetricAlerts metricAlerts) {
        if (metricAlerts == null) {
            return null;
        }
        try {
            return Json.of(objectMapper.writeValueAsString(metricAlerts));
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Error converting MetricAlerts to JSON", e);
        }
    }

    @Named("jsonToMetricAlerts")
    protected MetricAlerts jsonToMetricAlerts(Json json) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json.asString(), MetricAlerts.class);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Error converting JSON to MetricAlerts", e);
        }
    }
}