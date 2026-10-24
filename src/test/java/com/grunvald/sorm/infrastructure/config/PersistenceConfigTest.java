package com.grunvald.sorm.infrastructure.config;

import com.querydsl.sql.PostgreSQLTemplates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistenceConfigTest {

    @Test
    void postgresSqlSerializerConfig_usesPostgresTemplates() {
        var configuration = new PersistenceConfig().postgresSqlSerializerConfig();

        assertEquals(PostgreSQLTemplates.class, configuration.getTemplates().getClass());
    }
}
