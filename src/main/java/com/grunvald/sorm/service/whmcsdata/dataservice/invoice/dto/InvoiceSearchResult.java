package com.grunvald.sorm.service.whmcsdata.dataservice.invoice.dto;

import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;

import java.util.List;


public record InvoiceSearchResult(String cursor, boolean hasNextPage, List<Invoice> invoiceList) {
}