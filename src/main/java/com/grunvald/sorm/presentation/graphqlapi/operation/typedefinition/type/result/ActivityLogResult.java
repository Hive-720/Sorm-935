package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ActivityLog;

import java.util.List;


public record ActivityLogResult(String cursor, Boolean hasNextPage, List<ActivityLog> result) {
}