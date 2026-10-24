package com.grunvald.sorm.infrastructure.whmcsprovider.api;

import com.grunvald.sorm.infrastructure.config.WhmcsClientConfig;
import com.grunvald.sorm.infrastructure.config.WhmcsPropertiesConfig;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.request.GetActivityLogRequest;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.request.GetPaymentMethodRequest;
import com.grunvald.sorm.service.whmcsprovider.ProviderException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static com.grunvald.sorm.service.whmcsprovider.ProviderException.Error.EXTERNAL_ERROR;
import static com.grunvald.sorm.service.whmcsprovider.ProviderException.Error.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WhmcsApiClientTest {

    private MockWebServer server;
    private WhmcsApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        var credentials = new WhmcsPropertiesConfig.Credentials();
        credentials.setIdentifier("identifier-1");
        credentials.setSecret("secret-1");
        var props = new WhmcsPropertiesConfig();
        props.setCredentials(credentials);

        var config = new WhmcsClientConfig(props);
        var webClient = WebClient.builder().baseUrl(server.url("/").toString()).build();
        client = new WhmcsApiClient(webClient, config);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void request_success_sendsCredentialsInFormAndParsesBody() throws Exception {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {"result":"success","paymentmethods":{"paymentmethod":[
                          {"module":"paypal","displayname":"PayPal"}]}}
                        """));

        var result = client.request(new GetPaymentMethodRequest()).block();

        assertNotNull(result);
        assertEquals("PayPal", result.getPaymentMethods().get(0).getDisplayName());

        var body = server.takeRequest().getBody().readUtf8();
        assertTrue(body.contains("action=GetPaymentMethods"));
        assertTrue(body.contains("identifier=identifier-1"));
        assertTrue(body.contains("secret=secret-1"));
        assertTrue(body.contains("responsetype=json"));
        assertFalse(body.contains("limitstart"));
        assertFalse(body.contains("limitnum"));
    }

    @Test
    void request_withOffsetAndLimit_addsLimitstartAndLimitnum() throws Exception {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"result\":\"success\",\"activity\":{\"entry\":[]}}"));

        client.request(new GetActivityLogRequest(5L, 10)).block();

        var body = server.takeRequest().getBody().readUtf8();
        assertTrue(body.contains("action=GetActivityLog"));
        assertTrue(body.contains("limitstart=5"));
        assertTrue(body.contains("limitnum=10"));
    }

    @Test
    void request_bodyStatusError_mapsMessageToProviderException() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"result\":\"error\",\"message\":\"Client not found\"}"));

        StepVerifier.create(client.request(new GetPaymentMethodRequest()))
                .expectErrorSatisfies(e -> {
                    var ex = (ProviderException) e;
                    assertEquals(NOT_FOUND, ex.getError());
                    assertEquals("Client not found", ex.getMessage());
                })
                .verify();
    }

    @Test
    void request_bodyStatusError_unknownMessage_mapsToExternalError() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"result\":\"error\",\"message\":\"invalid api command\"}"));

        StepVerifier.create(client.request(new GetPaymentMethodRequest()))
                .expectErrorSatisfies(e -> assertEquals(EXTERNAL_ERROR, ((ProviderException) e).getError()))
                .verify();
    }

    @Test
    void request_httpError_mapsBodyMessageToProviderException() {
        server.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"result\":\"error\",\"message\":\"Client not found\"}"));

        StepVerifier.create(client.request(new GetPaymentMethodRequest()))
                .expectErrorSatisfies(e -> assertEquals(NOT_FOUND, ((ProviderException) e).getError()))
                .verify();
    }

    @Test
    void request_httpErrorWithEmptyBody_mapsToExternalError() {
        server.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(client.request(new GetPaymentMethodRequest()))
                .expectErrorSatisfies(e -> {
                    var ex = (ProviderException) e;
                    assertEquals(EXTERNAL_ERROR, ex.getError());
                    assertTrue(ex.getMessage().contains("500"));
                })
                .verify();
    }

    @Test
    void request_emptySuccessBody_mapsToExternalError() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(""));

        StepVerifier.create(client.request(new GetPaymentMethodRequest()))
                .expectErrorSatisfies(e -> assertEquals(EXTERNAL_ERROR, ((ProviderException) e).getError()))
                .verify();
    }
}
