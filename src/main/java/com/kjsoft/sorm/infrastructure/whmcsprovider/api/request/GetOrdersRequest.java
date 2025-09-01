package com.kjsoft.sorm.infrastructure.whmcsprovider.api.request;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetOrdersResponse;

import static com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_ORDERS;


public class GetOrdersRequest extends Request<GetOrdersResponse> {

    public GetOrdersRequest(Long offset, Integer limit) {
        super(offset, limit);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_ORDERS;
    }

    @Override
    public Class<GetOrdersResponse> getClassResponse() {
        return GetOrdersResponse.class;
    }
}