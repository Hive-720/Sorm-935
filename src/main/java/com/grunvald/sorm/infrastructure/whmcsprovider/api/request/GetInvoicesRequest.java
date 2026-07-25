package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetInvoicesResponse;
import org.springframework.web.reactive.function.BodyInserters;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_INVOICES;


public class GetInvoicesRequest extends Request<GetInvoicesResponse> {


    public GetInvoicesRequest(Long offset, Integer limit) {
        super(offset, limit);
    }

    public void addPostParams(BodyInserters.FormInserter<String> body) {
        body.with("orderby", "duedate");
        body.with("order", "desc");
    }

    @Override
    public WhmcsAction getAction() {
        return GET_INVOICES;
    }

    @Override
    public Class<GetInvoicesResponse> getClassResponse() {
        return GetInvoicesResponse.class;
    }
}