package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Affiliate;

import java.util.List;


public record AffiliateResult(String cursor, Boolean hasNextPage, List<Affiliate> result) {
}