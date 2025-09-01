package com.kjsoft.sorm.infrastructure.whmcsprovider.api.request;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.reactive.function.BodyInserters;


@Getter
@Setter
@AllArgsConstructor
public abstract class Request<T extends Response> {

    private Long offset;
    private Integer limit;

    public void addPostParams(BodyInserters.FormInserter<String> body) {
    }

    public abstract WhmcsAction getAction();

    public abstract Class<T> getClassResponse();
}