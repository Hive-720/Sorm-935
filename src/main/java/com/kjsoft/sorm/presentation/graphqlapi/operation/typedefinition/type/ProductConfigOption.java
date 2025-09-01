package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import java.util.List;


public record ProductConfigOption(
        Long id,
        Long productConfigId,
        String name,
        List<Price> prices
) {
}