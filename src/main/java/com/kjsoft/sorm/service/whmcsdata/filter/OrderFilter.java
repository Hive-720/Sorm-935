package com.kjsoft.sorm.service.whmcsdata.filter;

import com.kjsoft.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import com.kjsoft.sorm.service.whmcsdata.filter.base.IpAddressBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class OrderFilter {
    private ClientFilter client;
    private InvoiceFilter invoice;
    private OrderItemFilter items;
    private IpAddressBaseFilter ipAddress;
    private DateTimeBaseFilter date;
    private List<OrderFilter> and;
    private List<OrderFilter> or;
    private OrderFilter not;
}