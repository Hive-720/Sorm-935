package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;

import java.util.List;


public record MsisdnBaseFilter(
        StringRegExpExactFilter msisdn,
        List<MsisdnBaseFilter> and,
        List<MsisdnBaseFilter> or,
        MsisdnBaseFilter not
) {
}