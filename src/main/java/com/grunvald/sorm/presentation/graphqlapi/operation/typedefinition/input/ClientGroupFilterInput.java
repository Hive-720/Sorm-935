package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;

import java.util.List;


public record ClientGroupFilterInput(
        DateTimeBaseFilter updatedAt,
        List<ClientGroupFilterInput> and,
        List<ClientGroupFilterInput> or,
        ClientGroupFilterInput not
) {
}