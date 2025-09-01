package com.kjsoft.sorm.presentation.graphqlapi.operation.batchloader;

import com.kjsoft.sorm.service.whmcsdata.dataservice.transaction.TransactionService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.TransactionPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Transaction;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;


@Component
@RequiredArgsConstructor
public class TransactionBatchLoader implements MappedBatchLoader<Long, Transaction> {
    private final TransactionService service;
    private final TransactionPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, Transaction>> load(@NonNull Set<Long> transactionIds) {
        return service.getAllByIdIn(transactionIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collectMap(Transaction::id)
                .toFuture();
    }
}