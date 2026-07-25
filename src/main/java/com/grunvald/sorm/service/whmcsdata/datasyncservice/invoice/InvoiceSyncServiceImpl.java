package com.grunvald.sorm.service.whmcsdata.datasyncservice.invoice;

import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.InvoiceService;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.TransactionService;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.InvoiceMapper;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.TransactionMapper;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class InvoiceSyncServiceImpl implements InvoiceSyncService {
    private final InvoiceService invoiceService;
    private final TransactionService transactionService;
    private final WhmcsProvider whmcs;
    private final TransactionMapper transactionMapper;
    private final InvoiceMapper invoiceMapper;


    @Override
    public Mono<InvoiceSyncPage> getActualData(long offset, int limit, OffsetDateTime historyDepthDate) {
        return whmcs.getInvoices(offset, limit, historyDepthDate)
                .flatMap(result -> Flux.fromIterable(result.getElements())
                        .flatMap(invoice -> whmcs.getInvoiceAdditionalData(invoice.getId())
                                .zipWith(Mono.just(invoice))
                        )
                        .map(t2 -> {
                            var invoice = invoiceMapper.map(t2.getT2(), t2.getT1().getBalance(), t2.getT1().getItems());
                            var transactions = mapTransactions(t2.getT1().getTransactions(), invoice.getCurrency());
                            return Tuples.of(invoice, transactions);
                        })
                        .collect(
                                () -> Tuples.of(new ArrayList<Invoice>(), new ArrayList<Transaction>()),
                                (acc, currentTuple) -> {
                                    acc.getT1().add(currentTuple.getT1());
                                    acc.getT2().addAll(currentTuple.getT2());
                                }
                        )
                        .map(t2 -> InvoiceSyncPage.builder()
                                .invoices(t2.getT1())
                                .transactions(t2.getT2())
                                .hasNextPage(result.isHasNextPage())
                                .nextOffset(result.getNextOffset())
                                .build()
                        )
                );
    }

    private List<Transaction> mapTransactions(List<InvoiceAdditionalDataProviderDto.TransactionDto> transactions,
                                              String currency) {
        return transactions.stream()
                .map(dto -> transactionMapper.map(dto, currency))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Mono<Boolean> applyActualData(InvoiceSyncPage page) {
        return invoiceService.saveOrUpdateAll(page.getInvoices())
                .then(transactionService.saveOrUpdateAll(page.getTransactions()));
    }
}