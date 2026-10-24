package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;


@Setter
public class GetClientsResponse extends Response {
    private Clients clients;

    public List<Clients.Client> getClients() {
        return clients != null && clients.client != null ? clients.client : List.of();
    }

    @Getter
    @Setter
    public static class Clients {
        private List<Client> client;

        @Getter
        public static class Client {
            private Long id;
            @JsonProperty("firstname")
            private String firstName;
            @JsonProperty("lastname")
            private String lastName;
            @JsonProperty("companyname")
            private String companyName;
            private String email;
            @JsonProperty("datecreated")
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime dateCreated;
            @JsonProperty("groupid")
            private Long groupId;
            private String status;
        }
    }
}