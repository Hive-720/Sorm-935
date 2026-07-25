package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class WhmcsEmailListFromStrDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext context) throws IOException {
        var value = p.getText();

        if (!StringUtils.hasText(value)) {
            return null;
        }

        return Arrays.stream(value.split(","))
                .map(WhmcsEmailFromStrDeserializer::cleanEmail)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }
}