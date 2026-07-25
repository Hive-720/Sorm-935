package com.grunvald.sorm.presentation.config;

import com.grunvald.sorm.presentation.util.CustomLocaleContextResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.server.i18n.LocaleContextResolver;

import java.nio.charset.StandardCharsets;
import java.util.Locale;


@Configuration
public class LocaleConfig {

    @Bean
    public LocaleContextResolver localeContextResolver() {
        return new CustomLocaleContextResolver("en");
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        var source = new ResourceBundleMessageSource();
        source.setBasenames("messages/exception_message");
        source.setCacheSeconds(3600);
        source.setDefaultLocale(Locale.ENGLISH);
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }
}
