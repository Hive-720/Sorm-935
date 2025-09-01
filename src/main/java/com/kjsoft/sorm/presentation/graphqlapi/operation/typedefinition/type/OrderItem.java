package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DomainNameBase;


public record OrderItem(
        Long orderId,
        String type,
        Long relId,
        String productType,
        String productName,
        DomainNameBase domain,
        String billingCycle,
        String amount,
        String status
) {
}