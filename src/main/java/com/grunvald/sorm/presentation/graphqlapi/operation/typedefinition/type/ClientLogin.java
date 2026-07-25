package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;


public record ClientLogin(
        Long id,
        Long clientId,
        IpAddressBase ipAddress,
        DateTimeBase date
) {
}