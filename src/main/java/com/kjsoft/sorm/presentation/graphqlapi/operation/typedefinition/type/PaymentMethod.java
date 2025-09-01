package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;


public record PaymentMethod(
        Long id,
        String moduleName,
        String displayName,
        DateTimeBase updatedAt,
        Boolean deleted
) {
}