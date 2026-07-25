package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.enums.AlertStatus;

import java.util.List;


public record Alert(
        AlertStatus status,
        List<KeyValueRef> labels,
        List<KeyValueRef> annotations,
        DateTimeBase startAt,
        DateTimeBase endsAt,
        String generatorURL,
        String fingerprint
) {
}