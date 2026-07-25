package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.PaymentMethod;

import java.util.List;


public record PaymentMethodResult(String cursor, Boolean hasNextPage, List<PaymentMethod> result) {
}