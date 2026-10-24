package com.grunvald.sorm.presentation.graphqlapi.util;

import com.grunvald.sorm.presentation.graphqlapi.error.BadRequestException;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MapperOffsetDateTimeConverterTest {

    private final MapperOffsetDateTimeConverter converter = new MapperOffsetDateTimeConverter();

    @Test
    void stringToUtcOffsetDateTime_null_returnsNull() {
        assertNull(converter.stringToUtcOffsetDateTime(null));
    }

    @Test
    void stringToUtcOffsetDateTime_blank_returnsNull() {
        assertNull(converter.stringToUtcOffsetDateTime(""));
        assertNull(converter.stringToUtcOffsetDateTime("   "));
    }

    @Test
    void stringToUtcOffsetDateTime_unparseable_throwsBadRequestException() {
        assertThrows(BadRequestException.class, () -> converter.stringToUtcOffsetDateTime("not-a-date"));
        assertThrows(BadRequestException.class, () -> converter.stringToUtcOffsetDateTime("2026-13-45T99:99:99+03:00"));
    }

    @Test
    void stringToUtcOffsetDateTime_normalizesToUtc() {
        OffsetDateTime result = converter.stringToUtcOffsetDateTime("2026-01-02T03:04:05+03:00");

        assertEquals(OffsetDateTime.parse("2026-01-02T00:04:05Z"), result);
        assertEquals(ZoneOffset.UTC, result.getOffset());
    }

    @Test
    void stringToUtcOffsetDateTime_utcInput_isKept() {
        assertEquals(OffsetDateTime.parse("2026-01-02T00:04:05Z"), converter.stringToUtcOffsetDateTime("2026-01-02T00:04:05Z"));
    }

    @Test
    void offsetDateTimeToUtcString_null_returnsNull() {
        assertNull(converter.offsetDateTimeToUtcString(null));
    }

    @Test
    void offsetDateTimeToUtcString_convertsToUtcInIsoOffsetDateTimeFormat() {
        String result = converter.offsetDateTimeToUtcString(OffsetDateTime.parse("2026-01-02T03:04:05+03:00"));

        assertEquals("2026-01-02T00:04:05+00:00", result);
    }

    @Test
    void offsetDateTimeToUtcString_replacesZWithPlusZeroZero() {
        assertEquals("2026-01-02T00:04:05+00:00",
                converter.offsetDateTimeToUtcString(OffsetDateTime.parse("2026-01-02T00:04:05Z")));
    }

    @Test
    void stringListToUtcOffsetDateTimeList_null_returnsNull() {
        assertNull(converter.stringListToUtcOffsetDateTimeList(null));
    }

    @Test
    void stringListToUtcOffsetDateTimeList_empty_returnsNull() {
        assertNull(converter.stringListToUtcOffsetDateTimeList(List.of()));
    }

    @Test
    void stringListToUtcOffsetDateTimeList_filtersBlankAndNormalizesToUtc() {
        List<OffsetDateTime> result = converter.stringListToUtcOffsetDateTimeList(
                List.of("2026-01-02T03:04:05+03:00", "", "   ", "2026-01-03T00:00:00Z"));

        assertEquals(List.of(
                        OffsetDateTime.parse("2026-01-02T00:04:05Z"),
                        OffsetDateTime.parse("2026-01-03T00:00:00Z")),
                result);
    }

    @Test
    void stringListToUtcOffsetDateTimeList_allBlank_returnsEmptyList() {
        // фактическое поведение: после фильтрации blank остаётся пустой список (не null)
        assertEquals(List.of(), converter.stringListToUtcOffsetDateTimeList(List.of("", " ")));
    }
}
