package com.kjsoft.sorm.service.whmcsdata.filter;

import com.kjsoft.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PaymentMethodFilter {
    private DateTimeBaseFilter updatedAt;
    private List<PaymentMethodFilter> and;
    private List<PaymentMethodFilter> or;
    private PaymentMethodFilter not;
}