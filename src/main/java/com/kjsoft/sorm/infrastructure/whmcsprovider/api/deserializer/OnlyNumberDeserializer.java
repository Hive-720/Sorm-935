package com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.math.BigDecimal;


public class OnlyNumberDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        var value = jsonParser.getText();
        if (Strings.isBlank(value)) {
            return null;
        }

        var cleanedValue = value.replaceAll("[^0-9.]", "");
        if (cleanedValue.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(cleanedValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}