package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Affiliate;

import java.util.List;


public record AffiliateResult(String cursor, Boolean hasNextPage, List<Affiliate> result) {
}