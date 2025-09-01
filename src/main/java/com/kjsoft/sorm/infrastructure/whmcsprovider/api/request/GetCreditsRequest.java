package com.kjsoft.sorm.infrastructure.whmcsprovider.api.request;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetCreditsResponse;
import org.springframework.web.reactive.function.BodyInserters;

import static com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_CREDITS;


public class GetCreditsRequest extends Request<GetCreditsResponse> {

    private final long clientId;

    public GetCreditsRequest(long clientId) {
        super(null, null);
        this.clientId = clientId;
    }

    @Override
    public void addPostParams(BodyInserters.FormInserter<String> body) {
        body.with("clientid", String.valueOf(clientId));
    }

    @Override
    public WhmcsAction getAction() {
        return GET_CREDITS;
    }

    @Override
    public Class<GetCreditsResponse> getClassResponse() {
        return GetCreditsResponse.class;
    }
}