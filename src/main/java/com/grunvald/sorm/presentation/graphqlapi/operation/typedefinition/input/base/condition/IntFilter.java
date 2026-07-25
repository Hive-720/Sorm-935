package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition;

import java.util.List;


public record IntFilter(
        Integer eq,
        List<Integer> in,
        Integer le,
        Integer lt,
        Integer ge,
        Integer gt,
        IntRange between
) {
}