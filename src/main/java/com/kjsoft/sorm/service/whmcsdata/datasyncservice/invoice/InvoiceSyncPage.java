package com.kjsoft.sorm.service.whmcsdata.datasyncservice.invoice;

import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import com.kjsoft.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class InvoiceSyncPage {
    private List<Invoice> invoices;
    private List<Transaction> transactions;
    private boolean hasNextPage;
    private Long nextOffset;
}