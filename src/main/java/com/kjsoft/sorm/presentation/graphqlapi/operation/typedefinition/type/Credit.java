package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;


public record Credit(
        String id,
        Long clientId,
        String relId,
        String description,
        String amount,
        DateTimeBase date
) {
}