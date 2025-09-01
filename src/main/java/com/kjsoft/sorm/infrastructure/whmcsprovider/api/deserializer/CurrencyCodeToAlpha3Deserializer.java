package com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;

import static com.kjsoft.sorm.util.CurrencyUtil.getCurrency;


public class CurrencyCodeToAlpha3Deserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        var value = jsonParser.getText();

        if (Strings.isBlank(value)) {
            return null;
        }

        try {
            var numericCode = Integer.parseInt(value.trim());
            return getCurrency(numericCode);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}