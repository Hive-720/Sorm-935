package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;

import java.math.BigDecimal;


public record Order(
        Long id,
        Long clientId,
        Long contactId,
        Long invoiceId,
        BigDecimal amount,
        String currency,
        String status,
        IpAddressBase ipAddress,
        String paymentMethod,
        String paymentMethodName,
        String paymentStatus,
        DateTimeBase date
) {
}