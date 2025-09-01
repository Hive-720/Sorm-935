package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.*;

import java.util.List;


public record ContactFilterInput(
        ClientFilterInput client,
        PersonBaseFilter name,
        OrganizationInfoBaseFilter organizationInfo,
        EmailBaseFilter email,
        AddressBaseFilter address,
        InnBaseFilter inn,
        MsisdnBaseFilter msisdn,
        DateTimeBaseFilter updatedAt,
        DateTimeBaseFilter createdAt,
        List<ContactFilterInput> and,
        List<ContactFilterInput> or,
        ContactFilterInput not
) {
}