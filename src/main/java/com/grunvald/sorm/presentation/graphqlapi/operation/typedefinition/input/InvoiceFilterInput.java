package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;

import java.util.List;


public record InvoiceFilterInput(
        ClientFilterInput client,
        DateTimeBaseFilter date,
        DateTimeBaseFilter dueDate,
        DateTimeBaseFilter datePaid,
        DateTimeBaseFilter dateRefunded,
        DateTimeBaseFilter dateCancelled,
        DateTimeBaseFilter createdAt,
        DateTimeBaseFilter updatedAt,
        List<InvoiceFilterInput> and,
        List<InvoiceFilterInput> or,
        InvoiceFilterInput not
) {
}