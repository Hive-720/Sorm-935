package com.grunvald.sorm.infrastructure.persistence.data.email.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.email.entity.EmailDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.emailPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private EmailPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = emailPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        EmailDb db = new EmailDb();
        db.setId(1L);
        db.setClientId(2L);
        db.setSubject("Hello");
        db.setMessageBody("Body");
        db.setTo("a@example.com");
        db.setCc(new String[]{"c1@example.com", "c2@example.com"});
        db.setBcc(new String[]{"b1@example.com"});
        db.setDate(DATE);
        db.setCreatedAt(DATE);
        db.setUpdatedAt(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals("Hello", result.getSubject());
        assertEquals("Body", result.getMessageBody());
        assertEquals("a@example.com", result.getTo());
        assertEquals(List.of("c1@example.com", "c2@example.com"), result.getCc());
        assertEquals(List.of("b1@example.com"), result.getBcc());
        assertEquals(DATE, result.getDate());
        assertEquals(DATE, result.getCreatedAt());
        assertEquals(DATE, result.getUpdatedAt());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email();
        model.setId(1L);
        model.setClientId(2L);
        model.setSubject("Hello");
        model.setMessageBody("Body");
        model.setTo("a@example.com");
        model.setCc(List.of("c1@example.com", "c2@example.com"));
        model.setBcc(List.of("b1@example.com"));
        model.setDate(DATE);
        model.setCreatedAt(DATE);
        model.setUpdatedAt(DATE);

        EmailDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals("Hello", result.getSubject());
        assertEquals("Body", result.getMessageBody());
        assertEquals("a@example.com", result.getTo());
        assertArrayEquals(new String[]{"c1@example.com", "c2@example.com"}, result.getCc());
        assertArrayEquals(new String[]{"b1@example.com"}, result.getBcc());
        assertEquals(DATE, result.getDate());
        assertEquals(DATE, result.getCreatedAt());
        assertEquals(DATE, result.getUpdatedAt());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((EmailDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email) null));
    }
}
