package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;

import java.util.List;


public record InvoiceResult(String cursor, Boolean hasNextPage, List<Invoice> result) {
}