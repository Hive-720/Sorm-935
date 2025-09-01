package com.kjsoft.sorm.infrastructure.whmcsprovider.api.request;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetAffiliatesResponse;

import static com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_AFFILIATES;


public class GetAffiliatesRequest extends Request<GetAffiliatesResponse> {

    public GetAffiliatesRequest(Long offset, Integer limit) {
        super(offset, limit);
    }

    @Override
    public WhmcsAction getAction() {
        return GET_AFFILIATES;
    }

    @Override
    public Class<GetAffiliatesResponse> getClassResponse() {
        return GetAffiliatesResponse.class;
    }
}