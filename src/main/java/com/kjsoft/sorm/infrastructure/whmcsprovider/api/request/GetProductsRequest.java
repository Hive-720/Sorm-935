package com.kjsoft.sorm.infrastructure.whmcsprovider.api.request;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetProductsResponse;

import static com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_PRODUCTS;


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