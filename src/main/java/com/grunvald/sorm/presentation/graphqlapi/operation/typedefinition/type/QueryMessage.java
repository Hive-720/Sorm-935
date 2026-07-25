package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;


public record QueryMessage(String requestID, boolean offline, int maxPageSize) {
}