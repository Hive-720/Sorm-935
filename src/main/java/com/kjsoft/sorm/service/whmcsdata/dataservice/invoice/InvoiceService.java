package com.kjsoft.sorm.service.whmcsdata.dataservice.invoice;

import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.dto.InvoiceSearchResult;
import com.kjsoft.sorm.service.whmcsdata.filter.InvoiceFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface InvoiceService {

    Mono<List<Invoice>> getAllByIds(Set<Long> ids);

    Mono<Boolean> saveOrUpdateAll(List<Invoice> invoices);

    Mono<InvoiceSearchResult> search(InvoiceFilter filter, String cursor, Integer offset, Integer limit);
}