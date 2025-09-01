package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;

import java.math.BigDecimal;


public record Transaction(
        Long id,
        Long invoiceId,
        Long clientId,
        String transId,
        Long refundId,
        String currency,
        String gateway,
        String description,
        BigDecimal amountIn,
        BigDecimal amountOut,
        BigDecimal fees,
        BigDecimal rate,
        DateTimeBase date
) {
}