package com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod;

import com.kjsoft.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface PaymentMethodRepository {

    Mono<Boolean> addAll(List<PaymentMethod> paymentMethods);

    Mono<Boolean> updateAll(List<PaymentMethod> paymentMethods);

    Flux<PaymentMethod> findAll();

    Mono<PaymentMethodSearchResult> findAllByFilter(PaymentMethodFilter filter, String cursor, Integer offset, Integer limit);
}