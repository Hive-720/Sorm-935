package com.grunvald.sorm.infrastructure.whmcsprovider.api.request;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetInvoiceResponse;
import org.springframework.web.reactive.function.BodyInserters;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction.GET_INVOICE;


public class GetInvoiceRequest extends Request<GetInvoiceResponse> {

    private final long invoiceId;

    public GetInvoiceRequest(long invoiceId) {
        super(null, null);
        this.invoiceId = invoiceId;
    }

    @Override
    public void addPostParams(BodyInserters.FormInserter<String> body) {
        body.with("invoiceid", String.valueOf(invoiceId));
    }


    @Override
    public WhmcsAction getAction() {
        return GET_INVOICE;
    }

    @Override
    public Class<GetInvoiceResponse> getClassResponse() {
        return GetInvoiceResponse.class;
    }
}