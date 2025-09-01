package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;

import java.util.List;


public record ProductFilterInput(
        DateTimeBaseFilter updatedAt,
        List<ProductFilterInput> and,
        List<ProductFilterInput> or,
        ProductFilterInput not
) {
}