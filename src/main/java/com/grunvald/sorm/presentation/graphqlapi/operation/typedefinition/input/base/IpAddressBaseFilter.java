package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;

import java.util.List;


public record IpAddressBaseFilter(
        StringRegExpExactFilter ip,
        IpType type,
        List<IpAddressBaseFilter> and,
        List<IpAddressBaseFilter> or,
        IpAddressBaseFilter not
) {
}