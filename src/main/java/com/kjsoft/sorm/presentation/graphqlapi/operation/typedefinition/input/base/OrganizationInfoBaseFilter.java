package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;

import java.util.List;


public record OrganizationInfoBaseFilter(
        StringRegExpExactFilter nameFull,
        List<OrganizationInfoBaseFilter> and,
        List<OrganizationInfoBaseFilter> or,
        OrganizationInfoBaseFilter not
) {
}