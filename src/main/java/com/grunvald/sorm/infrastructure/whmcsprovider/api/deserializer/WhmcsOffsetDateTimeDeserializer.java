package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class WhmcsOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    );

    @Override
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        String dateString = parser.getValueAsString();
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        for (var formatter : FORMATTERS) {
            try {
                if (dateString.length() > 10) {
                    LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
                    return localDateTime.atOffset(ZoneOffset.UTC);
                } else {
                    LocalDate localDate = LocalDate.parse(dateString, formatter);
                    return localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
                }
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }
}