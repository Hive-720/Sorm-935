package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetProductsResponse;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_PRODUCTS;


public class GetProductsRequest extends Request<GetProductsResponse> {

    public GetProductsRequest() {
        super(null, null);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_PRODUCTS;
    }

    @Override
    public Class<GetProductsResponse> getClassResponse() {
        return GetProductsResponse.class;
    }
}