package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;


import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DomainNameBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;

import java.math.BigDecimal;
import java.util.List;


public record ClientProduct(
        String id,
        Long clientId,
        Long orderId,
        Long productId,
        String name,
        DomainNameBase domain,
        IpAddressBase dedicatedIpAddress,
        BigDecimal firstPaymentAmount,
        BigDecimal recurringAmount,
        String billingCycle,
        String status,
        List<IpAddressBase> assignedIpAddresses,
        List<ConfigValue> configs,
        DateTimeBase regDate,
        DateTimeBase nextDueDate,
        DateTimeBase lastUpdate
) {
}