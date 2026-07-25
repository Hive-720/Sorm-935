package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;


public record InvoiceItem(
        String type,
        String relId,
        String description,
        String amount,
        Boolean taxed
) {
}