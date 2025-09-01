package com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.util.StringUtils;

import java.io.IOException;


public class TrimDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        var value = p.getText();
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}