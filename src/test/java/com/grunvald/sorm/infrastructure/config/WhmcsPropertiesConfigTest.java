package com.grunvald.sorm.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class WhmcsPropertiesConfigTest {

    @Test
    void newConfig_createsClientWithDefaults_andLeavesCredentialsNull() {
        var config = new WhmcsPropertiesConfig();

        assertNull(config.getCredentials());
        assertNotNull(config.getClient());
    }

    @Test
    void newClientConfig_appliesDefaultTimeouts() {
        var client = new WhmcsPropertiesConfig.ClientConfig();

        assertNull(client.getUrl());
        assertEquals(60000, client.getConnectTimeout());
        assertEquals(60000L, client.getReadTimeout());
        assertEquals(60000L, client.getWriteTimeout());
        assertEquals(60000L, client.getResponseTimeout());
    }

    @Test
    void credentials_settersStoreValues() {
        var credentials = new WhmcsPropertiesConfig.Credentials();
        credentials.setIdentifier("id");
        credentials.setSecret("secret");

        assertEquals("id", credentials.getIdentifier());
        assertEquals("secret", credentials.getSecret());
    }

    @Test
    void clientConfig_settersStoreValues() {
        var client = new WhmcsPropertiesConfig.ClientConfig();
        client.setUrl("https://whmcs.example.com");
        client.setConnectTimeout(100);
        client.setReadTimeout(200L);
        client.setWriteTimeout(300L);
        client.setResponseTimeout(400L);

        assertEquals("https://whmcs.example.com", client.getUrl());
        assertEquals(100, client.getConnectTimeout());
        assertEquals(200L, client.getReadTimeout());
        assertEquals(300L, client.getWriteTimeout());
        assertEquals(400L, client.getResponseTimeout());
    }
}
