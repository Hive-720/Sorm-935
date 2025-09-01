package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.IpAddressBaseFilter;

import java.util.List;


public record OrderFilterInput(
        ClientFilterInput client,
        InvoiceFilterInput invoice,
        IpAddressBaseFilter ipAddress,
        OrderItemFilter items,
        DateTimeBaseFilter date,
        List<OrderFilterInput> and,
        List<OrderFilterInput> or,
        OrderFilterInput not
) {
}