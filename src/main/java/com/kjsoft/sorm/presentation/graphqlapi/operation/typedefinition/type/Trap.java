package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.kjsoft.sorm.graphql.generated.model.TrapTypeGql;


public record Trap(TrapTypeGql type, String message, DateTimeBase startsAt, MetricAlerts metricalerts) {
}