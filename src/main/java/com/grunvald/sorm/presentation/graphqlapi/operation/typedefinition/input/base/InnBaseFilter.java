package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;

import java.util.List;


public record InnBaseFilter(
        StringTermExactFilter inn,
        List<InnBaseFilter> and,
        List<InnBaseFilter> or,
        InnBaseFilter not
) {
}