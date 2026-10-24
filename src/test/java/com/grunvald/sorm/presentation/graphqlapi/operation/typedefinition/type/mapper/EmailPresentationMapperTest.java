package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Email;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.EmailBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.emailPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private EmailPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = emailPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email();
        source.setId(1L);
        source.setClientId(2L);
        source.setSubject("Hello");
        source.setMessageBody("Body");
        source.setTo("a@example.com");
        source.setCc(List.of("c1@example.com", "c2@example.com"));
        source.setBcc(List.of("b1@example.com"));
        source.setDate(DATE);
        source.setCreatedAt(DATE);
        source.setUpdatedAt(DATE);

        Email result = mapper.map(source);

        assertEquals(1L, result.id());
        assertEquals(2L, result.clientId());
        assertEquals("Hello", result.subject());
        assertEquals("Body", result.messageBody());
        assertEquals(new EmailBase("a@example.com"), result.to());
        assertEquals(List.of(new EmailBase("c1@example.com"), new EmailBase("c2@example.com")), result.cc());
        assertEquals(List.of(new EmailBase("b1@example.com")), result.bcc());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.date());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.createdAt());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.updatedAt());
    }

    @Test
    void shouldMapBlankCcEntriesFilteredOut() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email();
        source.setCc(List.of("   ", "c1@example.com"));

        Email result = mapper.map(source);

        assertEquals(List.of(new EmailBase("c1@example.com")), result.cc());
    }

    @Test
    void shouldMapEmptyCcAndBccToNull() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email();
        source.setCc(List.of());
        source.setBcc(List.of());

        Email result = mapper.map(source);

        assertNull(result.cc());
        assertNull(result.bcc());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
