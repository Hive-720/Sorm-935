package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.LoginBase;


public record ActivityLog(
        String id,
        Long clientId,
        Long userId,
        String description,
        LoginBase username,
        IpAddressBase ipAddress,
        DateTimeBase date
) {
}