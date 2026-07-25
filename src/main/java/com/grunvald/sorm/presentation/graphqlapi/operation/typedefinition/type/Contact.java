package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.*;


public record Contact(
        Long id,
        Long clientId,
        PersonBase name,
        OrganizationInfoBase organizationInfo,
        EmailBase email,
        AddressBase address,
        InnBase inn,
        MsisdnBase msisdn,
        DateTimeBase createdAt,
        DateTimeBase updatedAt,
        Boolean deleted
) {
}