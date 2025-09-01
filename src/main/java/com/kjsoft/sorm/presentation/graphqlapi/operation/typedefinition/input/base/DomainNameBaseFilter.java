package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;

import java.util.List;


public record DomainNameBaseFilter(
        StringRegExpExactFilter domainName,
        List<DomainNameBaseFilter> and,
        List<DomainNameBaseFilter> or,
        DomainNameBaseFilter not
) {
}