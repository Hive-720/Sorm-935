package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;

import java.util.List;


public record IpAddressBaseFilter(
        StringRegExpExactFilter ip,
        IpType type,
        List<IpAddressBaseFilter> and,
        List<IpAddressBaseFilter> or,
        IpAddressBaseFilter not
) {
}