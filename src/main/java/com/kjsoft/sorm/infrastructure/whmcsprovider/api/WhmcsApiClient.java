package com.kjsoft.sorm.infrastructure.whmcsprovider.api;

import com.kjsoft.sorm.service.whmcsprovider.ProviderException;
import com.kjsoft.sorm.infrastructure.config.WhmcsClientConfig;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.request.Request;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.BodyInserters.FormInserter;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.kjsoft.sorm.infrastructure.whmcsprovider.api.ErrorMapping.getErrorByMessage;
import static com.kjsoft.sorm.service.whmcsprovider.ProviderException.Error.EXTERNAL_ERROR;


@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsClientConfig.class)
public class WhmcsApiClient {
    private final WebClient whmcsWebClient;
    private final WhmcsClientConfig whmcsConfig;


    public <T extends Response> Mono<T> request(Request<T> request) {
        return whmcsWebClient.post()
                .body(buildFormData(request))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::processError)
                .bodyToMono(request.getClassResponse())
                .handle((response, sink) -> {
                    if ("error".equals(response.getStatus())) {
                        var errorCode = getErrorByMessage(response.getMessage());
                        sink.error(new ProviderException(errorCode, response.getMessage()));
                    } else {
                        sink.next(response);
                    }
                })
                .cast(request.getClassResponse())
                .switchIfEmpty(Mono.error(new ProviderException(EXTERNAL_ERROR, "Empty body")))
                .doOnError(e -> {
                    if (!(e instanceof ProviderException)) {
                        log.error("Failed to execute WHMCS request. Action: {}", request.getAction(), e);
                    }
                });
    }

    private Mono<? extends Throwable> processError(ClientResponse response) {
        final int statusCode = response.statusCode().value();
        return response.bodyToMono(Response.class)
                .flatMap(error -> {
                    log.error("WHMCS API HTTP Error {}: {} | Message: {}", statusCode, error.getStatus(), error.getMessage());
                    var errorCode = getErrorByMessage(error.getMessage());
                    return Mono.error(new ProviderException(errorCode, error.getMessage()));
                })
                .cast(Throwable.class);
    }


    private <T extends Response> FormInserter<String> buildFormData(Request<T> request) {
        var body = BodyInserters.fromFormData("action", request.getAction().getNameAction())
                .with("identifier", whmcsConfig.getWhmcsConfig().getCredentials().getIdentifier())
                .with("secret", whmcsConfig.getWhmcsConfig().getCredentials().getSecret())
                .with("responsetype", "json");

        if (request.getOffset() != null) {
            body.with("limitstart", String.valueOf(request.getOffset()));
        }

        if (request.getLimit() != null) {
            body.with("limitnum", String.valueOf(request.getLimit()));
        }

        request.addPostParams(body);

        return body;
    }
}