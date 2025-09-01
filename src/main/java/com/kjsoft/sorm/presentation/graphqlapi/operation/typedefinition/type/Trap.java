package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.enums.TrapType;


public record Trap(TrapType type, String message, DateTimeBase startsAt, MetricAlerts metricalerts) {
}