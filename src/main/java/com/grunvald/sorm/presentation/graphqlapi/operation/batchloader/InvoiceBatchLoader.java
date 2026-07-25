package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.InvoiceService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.InvoicePresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;
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
public class InvoiceBatchLoader implements MappedBatchLoader<Long, Invoice> {
    private final InvoiceService service;
    private final InvoicePresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, Invoice>> load(@NonNull Set<Long> invoiceIds) {
        return service.getAllByIds(invoiceIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collectMap(Invoice::id)
                .toFuture();
    }
}