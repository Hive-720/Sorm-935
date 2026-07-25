package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientProductsResponse;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_CLIENT_PRODUCTS;


public class GetClientProductsRequest extends Request<GetClientProductsResponse> {

    public GetClientProductsRequest(Long offset, Integer limit) {
        super(offset, limit);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_CLIENT_PRODUCTS;
    }

    @Override
    public Class<GetClientProductsResponse> getClassResponse() {
        return GetClientProductsResponse.class;
    }
}