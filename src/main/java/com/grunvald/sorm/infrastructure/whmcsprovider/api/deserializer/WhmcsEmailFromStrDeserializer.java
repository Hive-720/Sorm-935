package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.util.StringUtils;

import java.io.IOException;


public class WhmcsEmailFromStrDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext context) throws IOException {
        var value = p.getText();
        return cleanEmail(value);
    }

    public static String cleanEmail(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        value = value.trim();

        var openBracket = value.lastIndexOf('<');
        var closeBracket = value.lastIndexOf('>');

        if (openBracket >= 0 && closeBracket > openBracket) {
            return value.substring(openBracket + 1, closeBracket).trim();
        }

        return value;
    }
}