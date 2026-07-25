package com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod;

import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.grunvald.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;


public interface PaymentMethodService {

    Mono<PaymentMethodSearchResult> search(PaymentMethodFilter filter, String cursor, Integer offset, Integer limit);

    Mono<Boolean> saveOrUpdateAll(List<PaymentMethod> paymentMethod);
}