package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.service.system.notification.model.MetricAlerts;
import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Alert;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Trap;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface TrapMapper {

    @Mapping(source = "createdAt", target = "startsAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(source = "metricAlerts", target = "metricalerts")
    Trap map(SystemNotification systemNotification);

    @Mapping(source = "alertStatus", target = "status")
    @Mapping(source = "startAt", target = "startAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(source = "endsAt", target = "endsAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Alert mapAlert(MetricAlerts.Alert source);
}