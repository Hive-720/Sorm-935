package com.grunvald.sorm.infrastructure.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

class WhmcsClientConfigTest {

    private DisposableServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.disposeNow();
        }
    }

    private record Captured(String path, String contentType, String accept, String userAgent) {
    }

    @Test
    void whmcsWebClient_routesThroughBaseUrlAndSendsDefaultHeaders() {
        var captured = new AtomicReference<Captured>();
        server = HttpServer.create()
                .port(0)
                .handle((request, response) -> {
                    var headers = request.requestHeaders();
                    captured.set(new Captured(
                            request.fullPath(),
                            headers.get(CONTENT_TYPE),
                            headers.get(ACCEPT),
                            headers.get(USER_AGENT)));
                    return response.sendString(Mono.just("ok"));
                })
                .bindNow();

        var webClient = new WhmcsClientConfig(configWithUrl("http://localhost:" + server.port() + "/whmcs/"))
                .whmcsWebClient(WebClient.builder(), buildProperties("sorm-935", "1.2.3"));

        String body = webClient.get().uri("action")
                .exchangeToMono(response -> response.bodyToMono(String.class))
                .block();

        assertEquals("ok", body);
        var c = captured.get();
        assertEquals("/whmcs/action", c.path());
        assertEquals(APPLICATION_FORM_URLENCODED_VALUE, c.contentType());
        assertEquals(APPLICATION_JSON_VALUE, c.accept());
        assertEquals("sorm-935/1.2.3", c.userAgent());
    }

    @Test
    void whmcsWebClient_buildsHttpClientWithConfiguredTimeouts() {
        var properties = new WhmcsPropertiesConfig();
        var client = new WhmcsPropertiesConfig.ClientConfig();
        client.setUrl("http://localhost:1");
        client.setConnectTimeout(111);
        client.setReadTimeout(222L);
        client.setWriteTimeout(333L);
        client.setResponseTimeout(444L);
        properties.setClient(client);

        var webClient = new WhmcsClientConfig(properties)
                .whmcsWebClient(WebClient.builder(), buildProperties("sorm-935", "1.2.3"));

        assertNotNull(webClient);
    }

    private static WhmcsPropertiesConfig configWithUrl(String url) {
        var properties = new WhmcsPropertiesConfig();
        var client = new WhmcsPropertiesConfig.ClientConfig();
        client.setUrl(url);
        properties.setClient(client);
        return properties;
    }

    private static BuildProperties buildProperties(String name, String version) {
        var props = new Properties();
        props.setProperty("name", name);
        props.setProperty("version", version);
        return new BuildProperties(props);
    }
}
