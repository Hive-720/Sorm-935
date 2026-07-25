package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.enums.TrapType;


public record Trap(TrapType type, String message, DateTimeBase startsAt, MetricAlerts metricalerts) {
}