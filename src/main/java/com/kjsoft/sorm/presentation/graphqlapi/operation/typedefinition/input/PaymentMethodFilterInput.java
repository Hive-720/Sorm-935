package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;

import java.util.List;


public record PaymentMethodFilterInput(
        DateTimeBaseFilter updatedAt,
        List<PaymentMethodFilterInput> and,
        List<PaymentMethodFilterInput> or,
        PaymentMethodFilterInput not
) {
}