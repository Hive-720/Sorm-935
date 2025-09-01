package com.kjsoft.sorm.service.whmcsdata.dataservice.invoice;

import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.dto.InvoiceSearchResult;
import com.kjsoft.sorm.service.whmcsdata.filter.InvoiceFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface InvoiceRepository {

    Mono<Boolean> addAll(List<Invoice> invoices);

    Mono<Boolean> updateAll(List<Invoice> invoices);

    Flux<Invoice> findAllByIdIn(Set<Long> ids);

    Mono<InvoiceSearchResult> findAllByFilter(InvoiceFilter filter, String cursor, Integer offset, Integer limit);
}