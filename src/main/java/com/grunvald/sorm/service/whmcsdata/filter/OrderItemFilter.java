package com.grunvald.sorm.service.whmcsdata.filter;

import com.grunvald.sorm.service.whmcsdata.filter.base.DomainNameBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class OrderItemFilter {
    private DomainNameBaseFilter domain;
    private List<OrderItemFilter> and;
    private List<OrderItemFilter> or;
    private OrderItemFilter not;
}