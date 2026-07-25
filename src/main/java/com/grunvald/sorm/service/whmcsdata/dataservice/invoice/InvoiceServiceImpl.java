package com.grunvald.sorm.service.whmcsdata.dataservice.invoice;

import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.dto.InvoiceSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.InvoiceFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
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
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository repository;


    @Override
    public Mono<List<Invoice>> getAllByIds(@NonNull Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllByIdIn(ids)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAll(@NonNull List<Invoice> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        var ids = extractIds(incomingList);

        return repository.findAllByIdIn(ids)
                .collectMap(Invoice::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<Invoice>();
                    var toUpdate = new ArrayList<Invoice>();

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
    public Mono<InvoiceSearchResult> search(InvoiceFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllByFilter(filter, cursor, offset, limit);
    }

    private Set<Long> extractIds(List<Invoice> invoices) {
        return invoices.stream()
                .map(Invoice::getId)
                .collect(Collectors.toSet());
    }
}