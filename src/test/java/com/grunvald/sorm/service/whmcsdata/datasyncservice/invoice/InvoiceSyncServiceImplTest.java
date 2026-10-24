package com.grunvald.sorm.service.whmcsdata.datasyncservice.invoice;

import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.InvoiceService;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.TransactionService;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.InvoiceMapper;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.TransactionMapper;
import com.grunvald.sorm.service.whmcsprovider.result.InvoicesResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceSyncServiceImplTest {

    private static final OffsetDateTime HISTORY_DEPTH = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    @Mock
    private InvoiceService invoiceService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private InvoiceMapper invoiceMapper;

    private InvoiceSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InvoiceSyncServiceImpl(invoiceService, transactionService, whmcs, transactionMapper, invoiceMapper);
    }

    private InvoiceProviderDto invoiceDto(long id) {
        var dto = new InvoiceProviderDto();
        dto.setId(id);
        return dto;
    }

    private Invoice invoice(long id, String currency) {
        var i = new Invoice();
        i.setId(id);
        i.setCurrency(currency);
        return i;
    }

    private InvoiceAdditionalDataProviderDto.TransactionDto transactionDto(long id) {
        var dto = new InvoiceAdditionalDataProviderDto.TransactionDto();
        dto.setId(id);
        return dto;
    }

    private Transaction transaction(long id) {
        var t = new Transaction();
        t.setId(id);
        return t;
    }

    @Test
    void getActualData_mapsInvoicesAndTheirTransactions() {
        var invDto = invoiceDto(100L);
        var txDto = transactionDto(200L);
        var additional = InvoiceAdditionalDataProviderDto.builder()
                .balance(new BigDecimal("50"))
                .items(List.of())
                .transactions(List.of(txDto))
                .build();
        var mappedInvoice = invoice(100L, "RUB");
        var mappedTx = transaction(200L);

        when(whmcs.getInvoices(0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(InvoicesResult.builder().elements(List.of(invDto)).hasNextPage(false).nextOffset(null).build()));
        when(whmcs.getInvoiceAdditionalData(100L)).thenReturn(Mono.just(additional));
        when(invoiceMapper.map(invDto, additional.getBalance(), additional.getItems())).thenReturn(mappedInvoice);
        when(transactionMapper.map(txDto, "RUB")).thenReturn(mappedTx);

        StepVerifier.create(service.getActualData(0L, 10, HISTORY_DEPTH))
                .assertNext(page -> {
                    assertEquals(List.of(mappedInvoice), page.getInvoices());
                    assertEquals(List.of(mappedTx), page.getTransactions());
                    assertFalse(page.isHasNextPage());
                })
                .verifyComplete();
    }

    @Test
    void getActualData_emptyResult_returnsEmptyLists() {
        when(whmcs.getInvoices(0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(InvoicesResult.builder().elements(List.of()).hasNextPage(true).nextOffset(10L).build()));

        StepVerifier.create(service.getActualData(0L, 10, HISTORY_DEPTH))
                .assertNext(page -> {
                    assertTrue(page.getInvoices().isEmpty());
                    assertTrue(page.getTransactions().isEmpty());
                    assertTrue(page.isHasNextPage());
                    assertEquals(10L, page.getNextOffset());
                })
                .verifyComplete();
    }

    @Test
    void getActualData_emptyTransactionsList_mapsNoTransactions() {
        var invDto = invoiceDto(100L);
        var additional = InvoiceAdditionalDataProviderDto.builder()
                .balance(null)
                .items(List.of())
                .transactions(List.of())
                .build();
        var mappedInvoice = invoice(100L, "USD");

        when(whmcs.getInvoices(0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(InvoicesResult.builder().elements(List.of(invDto)).hasNextPage(false).nextOffset(null).build()));
        when(whmcs.getInvoiceAdditionalData(100L)).thenReturn(Mono.just(additional));
        when(invoiceMapper.map(any(), any(), any())).thenReturn(mappedInvoice);

        StepVerifier.create(service.getActualData(0L, 10, HISTORY_DEPTH))
                .assertNext(page -> {
                    assertEquals(1, page.getInvoices().size());
                    assertTrue(page.getTransactions().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void applyActualData_savesInvoicesThenTransactions() {
        var page = InvoiceSyncPage.builder()
                .invoices(List.of(invoice(1, "RUB")))
                .transactions(List.of(transaction(2)))
                .build();
        when(invoiceService.saveOrUpdateAll(page.getInvoices())).thenReturn(Mono.just(true));
        when(transactionService.saveOrUpdateAll(page.getTransactions())).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();

        verify(invoiceService).saveOrUpdateAll(page.getInvoices());
        verify(transactionService).saveOrUpdateAll(page.getTransactions());
    }
}
