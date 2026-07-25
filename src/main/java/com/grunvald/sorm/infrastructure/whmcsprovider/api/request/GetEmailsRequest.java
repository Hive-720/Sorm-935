package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetEmailsResponse;
import org.springframework.web.reactive.function.BodyInserters;


public class GetEmailsRequest extends Request<GetEmailsResponse> {

    private final long clientId;

    public GetEmailsRequest(long clientId, Long offset, Integer limit) {
        super(offset, limit);
        this.clientId = clientId;
    }

    @Override
    public void addPostParams(BodyInserters.FormInserter<String> body) {
        body.with("clientid", String.valueOf(clientId));
    }

    @Override
    public WhmcsAction getAction() {
        return WhmcsAction.GET_EMAILS;
    }

    @Override
    public Class<GetEmailsResponse> getClassResponse() {
        return GetEmailsResponse.class;
    }
}