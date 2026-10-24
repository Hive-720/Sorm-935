package com.grunvald.sorm.api;

import com.grunvald.sorm.SormIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientLoginsIdentityIntegrationTest extends SormIntegrationTest {

    @Test
    @DisplayName("INSERT без id генерирует identity id — addAllClientLogin не падает")
    void insertWithoutId_generatesIdentityId() throws SQLException {
        executeSql("""
                        INSERT INTO sorm_schema.client_logins (client_id, ip_address, date)
                        VALUES (1, '10.0.0.1', '2026-01-01T10:00:00+00:00'::timestamptz)
                        """);
        executeSql("""
                        INSERT INTO sorm_schema.client_logins (client_id, ip_address, date)
                        VALUES (1, '10.0.0.2', '2026-01-01T11:00:00+00:00'::timestamptz)
                        """);

        assertEquals(2, countRows("client_logins"));

        var ids = selectIds();
        assertEquals(2, ids.size());
        assertTrue(ids.get(0) > 0, "id должен генерироваться identity");
        assertTrue(ids.get(1) > ids.get(0), "id должны возрастать");
    }

    private List<Long> selectIds() throws SQLException {
        try (var connection = openConnection();
             var statement = connection.createStatement();
             var rs = statement.executeQuery("SELECT id FROM sorm_schema.client_logins ORDER BY id")) {
            var ids = new ArrayList<Long>();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
            return ids;
        }
    }
}
