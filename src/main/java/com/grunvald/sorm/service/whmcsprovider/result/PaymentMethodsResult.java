package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class PaymentMethodsResult {
    private List<PaymentMethodProviderDto> elements;
    private boolean hashNextPage;
    private Long nextOffset;
}