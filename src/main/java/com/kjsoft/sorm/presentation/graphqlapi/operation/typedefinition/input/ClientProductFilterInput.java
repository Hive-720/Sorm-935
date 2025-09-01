package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DomainNameBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.IpAddressBaseFilter;

import java.util.List;


public record ClientProductFilterInput(
        ClientFilterInput client,
        OrderFilterInput order,
        ProductFilterInput product,
        DomainNameBaseFilter domain,
        IpAddressBaseFilter dedicatedIpAddress,
        IpAddressBaseFilter assignedIpAddresses,
        DateTimeBaseFilter regDate,
        DateTimeBaseFilter nextDueDate,
        DateTimeBaseFilter lastUpdate,
        List<ClientProductFilterInput> and,
        List<ClientProductFilterInput> or,
        ClientProductFilterInput not
) {
}