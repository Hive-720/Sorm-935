package com.grunvald.sorm.infrastructure.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "sorm.whmcs")
@ConditionalOnProperty(name = "sorm.whmcs.data_sync.enabled", havingValue = "true", matchIfMissing = true)
public class WhmcsPropertiesConfig {
    @Valid
    private Credentials credentials;
    @Valid
    private ClientConfig client = new ClientConfig();


    @Getter
    @Setter
    public static class Credentials {
        @NotBlank
        private String identifier;
        @NotBlank
        private String secret;
    }

    @Getter
    @Setter
    public static class ClientConfig {
        @URL
        @Pattern(regexp = "^.*[^/]$", message = "URL не должен заканчиваться слешем")
        private String url;
        @Positive
        private Integer connectTimeout = 60000;
        @Positive
        private Long readTimeout = 60000L;
        @Positive
        private Long writeTimeout = 60000L;
        @Positive
        private Long responseTimeout = 60000L;
    }
}