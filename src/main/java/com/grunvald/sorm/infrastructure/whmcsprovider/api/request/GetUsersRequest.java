package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetUsersResponse;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_USERS;


public class GetUsersRequest extends Request<GetUsersResponse> {

    public GetUsersRequest(Long offset, Integer limit) {
        super(offset, limit);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_USERS;
    }

    @Override
    public Class<GetUsersResponse> getClassResponse() {
        return GetUsersResponse.class;
    }
}