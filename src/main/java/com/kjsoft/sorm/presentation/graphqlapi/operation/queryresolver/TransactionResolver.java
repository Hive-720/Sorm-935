package com.kjsoft.sorm.presentation.graphqlapi.operation.queryresolver;

import com.kjsoft.sorm.service.whmcsdata.dataservice.transaction.TransactionService;
import com.kjsoft.sorm.graphql.generated.model.TransactionFilterInputGql;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.TransactionFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.TransactionResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.TransactionResultMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Transaction;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class TransactionResolver {
    private final TransactionService transactionService;
    private final TransactionFilterPresentationMapper transactionFilterMapper;
    private final TransactionResultMapper transactionResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<TransactionResult> transactions(@Argument TransactionFilterInputGql filter, @Argument Integer offset,
                                                @Argument Integer limit, @Argument String cursor) {
        var searchFilter = transactionFilterMapper.map(filter);
        return transactionService.search(searchFilter, cursor, offset, limit)
                .map(transactionResultMapper::map);
    }

    @SchemaMapping(typeName = "Transaction", field = "client")
    public Mono<Client> getClientForTransaction(Transaction transaction, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(transaction.clientId()));
    }

    @SchemaMapping(typeName = "Transaction", field = "invoice")
    public Mono<Invoice> getInvoiceForTransaction(Transaction transaction, DataLoader<Long, Invoice> dataLoader) {
        return Mono.fromFuture(dataLoader.load(transaction.invoiceId()));
    }
}