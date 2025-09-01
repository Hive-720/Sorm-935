package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;

import java.util.List;


public record TransactionFilterInput(
        InvoiceFilterInput invoice,
        ClientFilterInput client,
        DateTimeBaseFilter date,
        List<TransactionFilterInput> and,
        List<TransactionFilterInput> or,
        TransactionFilterInput not
) {
}