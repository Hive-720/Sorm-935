package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;

import java.math.BigDecimal;


public record Affiliate(
        Long id,
        Long clientId,
        Integer visitors,
        String payType,
        String payAmount,
        Boolean oneTime,
        BigDecimal balance,
        BigDecimal withdrawn,
        DateTimeBase date,
        DateTimeBase createdAt,
        DateTimeBase updatedAt
) {
}