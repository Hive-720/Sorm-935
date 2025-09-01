package com.kjsoft.sorm.service.whmcsdata.filter;

import com.kjsoft.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class TransactionFilter {
    private InvoiceFilter invoice;
    private ClientFilter client;
    private DateTimeBaseFilter date;
    private List<TransactionFilter> and;
    private List<TransactionFilter> or;
    private TransactionFilter not;
}