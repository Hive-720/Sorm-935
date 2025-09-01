package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.IpAddressBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.LoginBaseFilter;

import java.util.List;


public record ActivityLogFilterInput(
        ClientFilterInput client,
        UserFilterInput user,
        LoginBaseFilter username,
        IpAddressBaseFilter ipAddress,
        DateTimeBaseFilter date,
        List<ActivityLogFilterInput> and,
        List<ActivityLogFilterInput> or,
        ActivityLogFilterInput not
) {
}