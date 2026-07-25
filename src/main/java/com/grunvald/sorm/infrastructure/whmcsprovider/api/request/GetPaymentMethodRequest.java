package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetPaymentMethodResponse;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_PAYMENT_METHOD;


public class GetPaymentMethodRequest extends Request<GetPaymentMethodResponse> {

    public GetPaymentMethodRequest() {
        super(null, null);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_PAYMENT_METHOD;
    }

    @Override
    public Class<GetPaymentMethodResponse> getClassResponse() {
        return GetPaymentMethodResponse.class;
    }
}