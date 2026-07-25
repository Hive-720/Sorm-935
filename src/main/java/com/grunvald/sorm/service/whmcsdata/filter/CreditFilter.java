package com.grunvald.sorm.service.whmcsdata.filter;

import com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CreditFilter {
    private ClientFilter client;
    private DateTimeBaseFilter date;
    private List<CreditFilter> and;
    private List<CreditFilter> or;
    private CreditFilter not;
}