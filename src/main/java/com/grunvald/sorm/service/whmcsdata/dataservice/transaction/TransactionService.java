package com.grunvald.sorm.service.whmcsdata.dataservice.transaction;

import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.dto.TransactionSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface TransactionService {

    Mono<List<Transaction>> getAllByIdIn(Set<Long> ids);

    Mono<Boolean> saveOrUpdateAll(List<Transaction> transactions);

    Mono<TransactionSearchResult> search(TransactionFilter filter, String cursor, Integer offset, Integer limit);
}