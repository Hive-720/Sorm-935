package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;

import java.math.BigDecimal;
import java.util.List;


public record Invoice(
        Long id,
        Long clientId,
        BigDecimal subTotal,
        BigDecimal credit,
        BigDecimal total,
        BigDecimal balance,
        BigDecimal tax,
        BigDecimal tax2,
        BigDecimal taxRate,
        BigDecimal taxRate2,
        String status,
        String paymentMethod,
        String currency,
        List<InvoiceItem> items,
        DateTimeBase date,
        DateTimeBase dueDate,
        DateTimeBase datePaid,
        DateTimeBase dateRefunded,
        DateTimeBase dateCancelled,
        DateTimeBase createdAt,
        DateTimeBase updatedAt
) {
}