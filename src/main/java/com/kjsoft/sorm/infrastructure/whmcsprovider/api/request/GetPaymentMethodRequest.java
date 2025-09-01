package com.kjsoft.sorm.infrastructure.whmcsprovider.api.request;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetPaymentMethodResponse;

import static com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_PAYMENT_METHOD;


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