package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;

import java.util.List;


public record CreditFilterInput(
        ClientFilterInput client,
        DateTimeBaseFilter date,
        List<CreditFilterInput> and,
        List<CreditFilterInput> or,
        CreditFilterInput not
) {
}