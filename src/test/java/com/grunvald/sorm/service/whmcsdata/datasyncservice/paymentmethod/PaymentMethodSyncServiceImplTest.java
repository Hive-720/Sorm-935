package com.grunvald.sorm.service.whmcsdata.datasyncservice.paymentmethod;

import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.PaymentMethodService;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.PaymentMethodMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentMethodSyncServiceImplTest {

    @Mock
    private PaymentMethodService paymentMethodService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private PaymentMethodMapper mapper;

    private PaymentMethodSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PaymentMethodSyncServiceImpl(paymentMethodService, whmcs, mapper);
    }

    @Test
    void getActualData_mapsAllPaymentMethods() {
        var dto1 = new PaymentMethodProviderDto();
        dto1.setModule("stripe");
        var dto2 = new PaymentMethodProviderDto();
        dto2.setModule("paypal");
        var pm1 = new PaymentMethod();
        pm1.setModule("stripe");
        var pm2 = new PaymentMethod();
        pm2.setModule("paypal");

        when(whmcs.getPaymentMethods()).thenReturn(Flux.just(dto1, dto2));
        when(mapper.map(dto1)).thenReturn(pm1);
        when(mapper.map(dto2)).thenReturn(pm2);

        StepVerifier.create(service.getActualData())
                .expectNext(List.of(pm1, pm2))
                .verifyComplete();
    }

    @Test
    void getActualData_emptySource_returnsEmptyList() {
        when(whmcs.getPaymentMethods()).thenReturn(Flux.empty());

        StepVerifier.create(service.getActualData())
                .assertNext(list -> assertTrue(list.isEmpty()))
                .verifyComplete();
    }

    @Test
    void applyActualData_delegatesToPaymentMethodService() {
        var data = List.of(new PaymentMethod());
        when(paymentMethodService.saveOrUpdateAll(data)).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(data))
                .expectNext(true)
                .verifyComplete();

        verify(paymentMethodService).saveOrUpdateAll(data);
    }
}
