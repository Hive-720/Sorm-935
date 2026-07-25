package com.grunvald.sorm.service.whmcsdata.dataservice.transaction;

import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.dto.TransactionSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository repository;


    @Override
    public Mono<List<Transaction>> getAllByIdIn(@NonNull Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllByIdIn(ids)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAll(@NonNull List<Transaction> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        var ids = extractIds(incomingList);
        return repository.findAllByIdIn(ids)
                .collectMap(Transaction::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<Transaction>();
                    var toUpdate = new ArrayList<Transaction>();

                    for (var actual : incomingList) {
                        var local = existingMap.get(actual.getId());

                        if (local == null) {
                            toSave.add(actual);
                        } else {
                            if (local.update(actual)) {
                                toUpdate.add(local);
                            }
                        }
                    }

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAll(toSave))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAll(toUpdate))
                            .then(Mono.just(true));
                });
    }

    @Override
    public Mono<TransactionSearchResult> search(TransactionFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllByFilter(filter, cursor, offset, limit);
    }

    private Set<Long> extractIds(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getId)
                .collect(Collectors.toSet());
    }
}