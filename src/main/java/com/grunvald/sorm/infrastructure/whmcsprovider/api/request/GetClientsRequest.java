package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientsResponse;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_CLIENTS;


public class GetClientsRequest extends Request<GetClientsResponse> {

    public GetClientsRequest(Long offset, Integer limit) {
        super(offset, limit);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_CLIENTS;
    }

    @Override
    public Class<GetClientsResponse> getClassResponse() {
        return GetClientsResponse.class;
    }
}