package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;

import java.math.BigDecimal;


public record ClientGroup(
        Long id,
        String name,
        BigDecimal discountPercent,
        DateTimeBase updatedAt,
        Boolean deleted
) {
}