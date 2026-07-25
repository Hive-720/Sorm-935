package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetActivityLogResponse;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_ACTIVITY_LOG;


public class GetActivityLogRequest extends Request<GetActivityLogResponse> {

    public GetActivityLogRequest(Long offset, Integer limit) {
        super(offset, limit);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_ACTIVITY_LOG;
    }

    @Override
    public Class<GetActivityLogResponse> getClassResponse() {
        return GetActivityLogResponse.class;
    }
}