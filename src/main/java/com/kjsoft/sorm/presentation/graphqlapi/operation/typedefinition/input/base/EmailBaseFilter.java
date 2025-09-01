package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;

import java.util.List;


public record EmailBaseFilter(
        StringRegExpExactFilter email,
        List<EmailBaseFilter> and,
        List<EmailBaseFilter> or,
        EmailBaseFilter not
) {
}