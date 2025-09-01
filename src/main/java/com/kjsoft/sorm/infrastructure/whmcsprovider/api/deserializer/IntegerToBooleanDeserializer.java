package com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;


public class IntegerToBooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        var value = jsonParser.getText();
        if (Strings.isBlank(value)) {
            return null;
        }

        try {
            var numericCode = Integer.parseInt(value.trim());
            return numericCode == 1;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}