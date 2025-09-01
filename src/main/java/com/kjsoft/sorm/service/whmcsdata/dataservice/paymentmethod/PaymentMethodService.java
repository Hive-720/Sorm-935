package com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod;

import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.kjsoft.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;


public interface PaymentMethodService {

    Mono<PaymentMethodSearchResult> search(PaymentMethodFilter filter, String cursor, Integer offset, Integer limit);

    Mono<Boolean> saveOrUpdateAll(List<PaymentMethod> paymentMethod);
}