package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.enums.AlertStatus;

import java.util.List;


public record MetricAlerts(
        String groupKey,
        AlertStatus status,
        String receiver,
        List<KeyValueRef> groupLabels,
        List<KeyValueRef> commonLabels,
        List<KeyValueRef> commonAnnotations,
        String externalURL,
        List<Alert> alerts
) {
}