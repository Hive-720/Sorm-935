package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;

import java.util.List;


public record OrganizationInfoBaseFilter(
        StringRegExpExactFilter nameFull,
        List<OrganizationInfoBaseFilter> and,
        List<OrganizationInfoBaseFilter> or,
        OrganizationInfoBaseFilter not
) {
}