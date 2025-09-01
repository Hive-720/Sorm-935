package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DomainNameBaseFilter;

import java.util.List;


public record OrderItemFilter(
        DomainNameBaseFilter domain,
        List<OrderItemFilter> and,
        List<OrderItemFilter> or,
        OrderItemFilter not
) {
}