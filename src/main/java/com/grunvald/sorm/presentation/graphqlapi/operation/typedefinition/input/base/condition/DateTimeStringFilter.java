package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition;

import java.util.List;


public record DateTimeStringFilter(
        String eq,
        List<String> in,
        String le,
        String lt,
        String ge,
        String gt,
        DateTimeRange between
) {
}