package com.kjsoft.sorm.service.whmcsdata.datasyncservice.paymentmethod;

import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import reactor.core.publisher.Mono;

import java.util.List;


public interface PaymentMethodSyncService {

    Mono<List<PaymentMethod>> getActualData();

    Mono<Boolean> applyActualData(List<PaymentMethod> data);
}