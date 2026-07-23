package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.kjsoft.sorm.graphql.generated.model.AlertStatusGql;

import java.util.List;


public record Alert(
        AlertStatusGql status,
        List<KeyValueRef> labels,
        List<KeyValueRef> annotations,
        DateTimeBase startAt,
        DateTimeBase endsAt,
        String generatorURL,
        String fingerprint
) {
}