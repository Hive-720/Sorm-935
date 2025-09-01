package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;

import java.util.List;


public record InnBaseFilter(
        StringTermExactFilter inn,
        List<InnBaseFilter> and,
        List<InnBaseFilter> or,
        InnBaseFilter not
) {
}