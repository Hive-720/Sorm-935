package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientGroupsResponse;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_CLIENT_GROUPS;


public class GetClientGroupRequest extends Request<GetClientGroupsResponse> {

    public GetClientGroupRequest() {
        super(null, null);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_CLIENT_GROUPS;
    }

    @Override
    public Class<GetClientGroupsResponse> getClassResponse() {
        return GetClientGroupsResponse.class;
    }
}