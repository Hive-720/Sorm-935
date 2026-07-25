package com.grunvald.sorm.service.whmcsdata.filter;

import com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class InvoiceFilter {
    private ClientFilter client;
    private DateTimeBaseFilter date;
    private DateTimeBaseFilter dueDate;
    private DateTimeBaseFilter datePaid;
    private DateTimeBaseFilter dateRefunded;
    private DateTimeBaseFilter dateCancelled;
    private DateTimeBaseFilter createdAt;
    private DateTimeBaseFilter updatedAt;
    private List<InvoiceFilter> and;
    private List<InvoiceFilter> or;
    private InvoiceFilter not;
}