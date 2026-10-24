package com.grunvald.sorm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {

    private final ObjectMapper mapper = new Config().objectMapper();

    static class Sample {
        public String name;
        public OffsetDateTime when;

        Sample() {
        }

        Sample(String name, OffsetDateTime when) {
            this.name = name;
            this.when = when;
        }
    }

    static class Unknown {
        public String known;
    }

    @Test
    void unknownProperties_ignored() throws Exception {
        var json = "{\"known\": \"a\", \"unknownField\": 42}";

        var result = mapper.readValue(json, Unknown.class);

        assertEquals("a", result.known);
    }

    @Test
    void nullFields_notSerialized() throws Exception {
        var json = mapper.writeValueAsString(new Sample(null, null));

        assertFalse(json.contains("name"));
        assertFalse(json.contains("when"));
    }

    @Test
    void javaTime_roundTrip() throws Exception {
        var odt = OffsetDateTime.of(2026, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC);

        var json = mapper.writeValueAsString(new Sample("x", odt));
        var restored = mapper.readValue(json, Sample.class);

        assertEquals("x", restored.name);
        assertEquals(odt.toInstant(), restored.when.toInstant());
    }
}
