package com.grunvald.sorm.service.whmcsdata.dataservice.transaction.dto;

import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;

import java.util.List;


public record TransactionSearchResult(String cursor, boolean hasNextPage, List<Transaction> transactionList) {
}