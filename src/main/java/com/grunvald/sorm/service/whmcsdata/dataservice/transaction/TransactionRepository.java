package com.grunvald.sorm.service.whmcsdata.dataservice.transaction;

import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.dto.TransactionSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface TransactionRepository {

    Mono<Boolean> addAll(List<Transaction> transactions);

    Mono<Boolean> updateAll(List<Transaction> transactions);

    Flux<Transaction> findAllByIdIn(Set<Long> ids);

    Mono<TransactionSearchResult> findAllByFilter(TransactionFilter filter, String cursor, Integer offset, Integer limit);
}