package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;

import java.util.List;


public record LoginBaseFilter(
        StringRegExpExactFilter login,
        List<LoginBaseFilter> and,
        List<LoginBaseFilter> or,
        LoginBaseFilter not
) {
}