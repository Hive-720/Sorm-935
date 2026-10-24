package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public final class DeserializerTestSupport {

    private DeserializerTestSupport() {
    }

    public static <T> T deserializeFromJson(JsonDeserializer<T> deserializer, String jsonLiteral) throws IOException {
        try (JsonParser parser = new JsonFactory().createParser(jsonLiteral)) {
            parser.nextToken();
            return deserializer.deserialize(parser, null);
        }
    }

    public static <T> T deserializeFromString(JsonDeserializer<T> deserializer, String value) throws IOException {
        String escaped = value == null ? "null"
                : "\"" + value.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t") + "\"";
        return deserializeFromJson(deserializer, escaped);
    }
}
