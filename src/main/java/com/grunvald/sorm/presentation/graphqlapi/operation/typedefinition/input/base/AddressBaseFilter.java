package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpFullTextFilter;

import java.util.List;


public record AddressBaseFilter(
        StringRegExpFullTextFilter address,
        StringExactFilter zip,
        StringExactFilter country,
        StringExactFilter region,
        StringExactFilter city,
        List<AddressBaseFilter> and,
        List<AddressBaseFilter> or,
        AddressBaseFilter not
) {
}