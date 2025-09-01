package com.kjsoft.sorm.presentation.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

import java.util.Locale;


@Slf4j
@RequiredArgsConstructor
public class CustomLocaleContextResolver implements LocaleContextResolver {
    private final String defaultLang;


    @NonNull
    @Override
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
        Locale locale;
        var lang = exchange.getRequest().getHeaders().getFirst("lang");

        if (lang == null) {
            lang = "en";
        }

        try {
            if (lang.length() > 2) {
                throw new IllegalArgumentException();
            }

            locale = Locale.forLanguageTag(lang);

            if (locale.getLanguage().isEmpty()) {
                throw new IllegalArgumentException();
            }

        } catch (Exception e) {
            log.debug("Lang {} invalid, only Alpha-2 Code is supported", lang);
            locale = Locale.forLanguageTag(defaultLang);
        }

        return new SimpleLocaleContext(locale);
    }

    @Override
    public void setLocaleContext(@NonNull ServerWebExchange exchange, LocaleContext localeContext) {
        throw new UnsupportedOperationException(
                "Cannot change HTTP accept header - use a different locale context resolution strategy");
    }
}