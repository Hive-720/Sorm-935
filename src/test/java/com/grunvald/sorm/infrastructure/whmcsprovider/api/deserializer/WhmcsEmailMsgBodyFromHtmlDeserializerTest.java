package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class WhmcsEmailMsgBodyFromHtmlDeserializerTest {

    private final WhmcsEmailMsgBodyFromHtmlDeserializer deserializer = new WhmcsEmailMsgBodyFromHtmlDeserializer();

    @Test
    void simpleHtml_textExtractedWithoutTags() throws Exception {
        var result = deserializeFromString(deserializer, "<p>Hello world</p>");

        assertEquals("Hello world", result);
        assertFalse(result.contains("<"));
    }

    @Test
    void br_becomesNewline() throws Exception {
        var result = deserializeFromString(deserializer, "line1<br>line2");

        // doc.text() jsoup добавляет пробелы вокруг вставленных переносов
        assertEquals("line1 \n line2", result);
    }

    @Test
    void paragraphs_separatedByBlankLine() throws Exception {
        var result = deserializeFromString(deserializer, "<p>one</p><p>two</p>");

        assertEquals("one \n\ntwo", result);
    }

    @Test
    void plainText_unchanged() throws Exception {
        assertEquals("just text", deserializeFromString(deserializer, "just text"));
    }

    @Test
    void blank_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
        assertNull(deserializeFromString(deserializer, "   "));
    }
}
