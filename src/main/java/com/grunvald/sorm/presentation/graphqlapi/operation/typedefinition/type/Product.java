package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;

import java.util.List;


public record Product(
        Long id,
        String type,
        String name,
        String description,
        String payType,
        List<Price> prices,
        DateTimeBase updatedAt,
        Boolean deleted
) {
}