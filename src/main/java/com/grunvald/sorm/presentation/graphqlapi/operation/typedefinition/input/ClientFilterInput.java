package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.*;

import java.util.List;


public record ClientFilterInput(
        PersonBaseFilter name,
        UserFilterInput userOwner,
        ClientGroupFilterInput clientGroup,
        OrganizationInfoBaseFilter organizationInfo,
        InnBaseFilter inn,
        EmailBaseFilter email,
        MsisdnBaseFilter msisdn,
        MccBaseFilter mcc,
        AddressBaseFilter address,
        DateTimeBaseFilter createdAt,
        List<ClientFilterInput> and,
        List<ClientFilterInput> or,
        ClientFilterInput not
) {
}