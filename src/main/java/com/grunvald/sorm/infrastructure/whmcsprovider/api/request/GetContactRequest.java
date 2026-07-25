package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetContactsResponse;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_CONTACTS;


public class GetContactRequest extends Request<GetContactsResponse> {

    public GetContactRequest(Long offset, Integer limit) {
        super(offset, limit);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_CONTACTS;
    }

    @Override
    public Class<GetContactsResponse> getClassResponse() {
        return GetContactsResponse.class;
    }
}