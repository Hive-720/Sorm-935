package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermFullTextFilter;

import java.util.List;


public record PersonBaseFilter(
        StringTermFullTextFilter fullName,
        StringTermExactFilter name,
        StringTermExactFilter lastName,
        List<PersonBaseFilter> and,
        List<PersonBaseFilter> or,
        PersonBaseFilter not
) {
}