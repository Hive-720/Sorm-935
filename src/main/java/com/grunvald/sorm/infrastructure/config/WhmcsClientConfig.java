package com.grunvald.sorm.infrastructure.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Getter
@Setter
@Configuration
@ConditionalOnBean(WhmcsPropertiesConfig.class)
@RequiredArgsConstructor
public class WhmcsClientConfig {
    private final WhmcsPropertiesConfig whmcsConfig;


    @Bean
    public WebClient whmcsWebClient(BuildProperties buildProperties) {
        var appVersion = buildProperties.getVersion();
        var appName = buildProperties.getName();
        return WebClient.builder()
                .baseUrl(whmcsConfig.getClient().getUrl())
                .defaultHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .defaultHeader(USER_AGENT, appName + "/" + appVersion)
                .clientConnector(new ReactorClientHttpConnector(buildHttpClient()))
                .build();
    }

    private HttpClient buildHttpClient() {
        return HttpClient.create()
                .option(CONNECT_TIMEOUT_MILLIS, whmcsConfig.getClient().getConnectTimeout())
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(whmcsConfig.getClient().getReadTimeout(), MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(whmcsConfig.getClient().getWriteTimeout(), MILLISECONDS))
                )
                .responseTimeout(Duration.ofMillis(whmcsConfig.getClient().getResponseTimeout()));
    }
}