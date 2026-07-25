package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.IntFilter;

import java.util.List;


public record MccBaseFilter(
        IntFilter mcc,
        List<MccBaseFilter> and,
        List<MccBaseFilter> or,
        MccBaseFilter not
) {
}