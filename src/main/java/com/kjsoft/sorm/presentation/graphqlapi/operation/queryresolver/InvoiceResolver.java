package com.kjsoft.sorm.presentation.graphqlapi.operation.queryresolver;

import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.InvoiceService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.InvoiceFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.InvoiceFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.InvoiceResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.InvoiceResultMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class InvoiceResolver {
    private final InvoiceService invoiceService;
    private final InvoiceFilterPresentationMapper invoiceFilterMapper;
    private final InvoiceResultMapper invoiceResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<InvoiceResult> invoices(@Argument InvoiceFilterInput filter, @Argument Integer offset,
                                        @Argument Integer limit, @Argument String cursor) {
        var searchFilter = invoiceFilterMapper.map(filter);
        return invoiceService.search(searchFilter, cursor, offset, limit)
                .map(invoiceResultMapper::map);
    }

    @SchemaMapping(typeName = "Invoice", field = "client")
    public Mono<Client> getClientForInvoice(Invoice invoice, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(invoice.clientId()));
    }
}