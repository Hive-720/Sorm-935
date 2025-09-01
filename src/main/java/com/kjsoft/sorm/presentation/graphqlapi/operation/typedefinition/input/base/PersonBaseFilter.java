package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermFullTextFilter;

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