package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.*;

import java.math.BigDecimal;


public record Client(
        Long id,
        Long ownerUserId,
        Long clientGroupId,
        PersonBase name,
        OrganizationInfoBase organizationInfo,
        InnBase inn,
        EmailBase email,
        AddressBase address,
        MsisdnBase msisdn,
        MccBase mcc,
        String phoneNumber,
        String defaultGateway,
        String telegram,
        String jabber,
        String currency,
        String status,
        BigDecimal credit,
        Boolean taxExempt,
        String language,
        DateTimeBase dateCreated,
        Boolean deleted
) {
}