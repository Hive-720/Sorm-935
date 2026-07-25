package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.DateTimeStringFilter;

import java.util.List;


public record DateTimeBaseFilter(
        DateTimeStringFilter utc,
        List<DateTimeBaseFilter> and,
        List<DateTimeBaseFilter> or,
        DateTimeBaseFilter not
) {
}