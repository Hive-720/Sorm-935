package com.kjsoft.sorm.service.whmcsdata.datasyncservice.paymentmethod;

import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.PaymentMethodService;
import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.kjsoft.sorm.service.whmcsprovider.dto.mapper.PaymentMethodMapper;
import com.kjsoft.sorm.service.whmcsprovider.WhmcsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class PaymentMethodSyncServiceImpl implements PaymentMethodSyncService {
    private final PaymentMethodService paymentMethodService;
    private final WhmcsProvider whmcs;
    private final PaymentMethodMapper mapper;


    @Override
    public Mono<List<PaymentMethod>> getActualData() {
        return whmcs.getPaymentMethods()
                .map(mapper::map)
                .collectList();
    }

    @Override
    public Mono<Boolean> applyActualData(List<PaymentMethod> data) {
        return paymentMethodService.saveOrUpdateAll(data);
    }
}