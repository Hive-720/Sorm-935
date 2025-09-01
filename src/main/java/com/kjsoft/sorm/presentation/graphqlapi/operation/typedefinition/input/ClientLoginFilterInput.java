package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.IpAddressBaseFilter;

import java.util.List;


public record ClientLoginFilterInput(
        ClientFilterInput client,
        IpAddressBaseFilter ipAddress,
        DateTimeBaseFilter date,
        List<ClientLoginFilterInput> and,
        List<ClientLoginFilterInput> or,
        ClientLoginFilterInput not
) {
}