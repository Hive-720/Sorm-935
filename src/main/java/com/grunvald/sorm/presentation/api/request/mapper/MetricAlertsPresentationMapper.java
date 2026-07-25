package com.grunvald.sorm.presentation.api.request.mapper;

import com.grunvald.sorm.service.system.notification.model.MetricAlerts;
import com.grunvald.sorm.presentation.api.request.MetricAlertsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface MetricAlertsPresentationMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    MetricAlerts map(MetricAlertsRequest metricAlerts);

    @Mapping(target = "alertStatus", source = "alertStatus", qualifiedByName = "stringToStatus")
    MetricAlerts.Alert mapAlert(MetricAlertsRequest.AlertRequest alert);

    @Named("stringToStatus")
    default MetricAlerts.Status stringToStatus(String status) {
        if (status == null) {
            return null;
        }

        return MetricAlerts.Status.valueOf(status.toUpperCase());
    }
}