package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;

import java.util.List;


public record AffiliateFilterInput(
        ClientFilterInput client,
        DateTimeBaseFilter date,
        DateTimeBaseFilter createdAt,
        DateTimeBaseFilter updatedAt,
        List<AffiliateFilterInput> and,
        List<AffiliateFilterInput> or,
        AffiliateFilterInput not
) {
}