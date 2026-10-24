package com.grunvald.sorm.presentation.util;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomLocaleContextResolverTest {

    private final CustomLocaleContextResolver resolver = new CustomLocaleContextResolver("ru");

    @Test
    void resolveLocaleContext_withoutHeader_defaultsToEnglish() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        assertEquals(Locale.ENGLISH, resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    void resolveLocaleContext_validAlpha2Code_isResolved() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/").header("lang", "ru"));

        assertEquals(Locale.forLanguageTag("ru"), resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    void resolveLocaleContext_moreThanTwoChars_fallsBackToDefaultLang() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/").header("lang", "ru-RU"));

        assertEquals(Locale.forLanguageTag("ru"), resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    void resolveLocaleContext_singleCharInvalidTag_fallsBackToDefaultLang() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/").header("lang", "x"));

        assertEquals(Locale.forLanguageTag("ru"), resolver.resolveLocaleContext(exchange).getLocale());
    }

    @Test
    void setLocaleContext_isNotSupported() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        assertThrows(UnsupportedOperationException.class,
                () -> resolver.setLocaleContext(exchange, new SimpleLocaleContext(Locale.ENGLISH)));
    }
}
