package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.PaymentMethod;

import java.util.List;


public record PaymentMethodResult(String cursor, Boolean hasNextPage, List<PaymentMethod> result) {
}