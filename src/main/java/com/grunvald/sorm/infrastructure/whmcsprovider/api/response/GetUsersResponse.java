package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;


public class GetUsersResponse extends Response {
    public List<User> users;

    public List<User> getUsers() {
        return users != null ? users : List.of();
    }


    @Getter
    public static class User {
        private Long id;
        @JsonProperty("firstname")
        private String firstName;
        @JsonProperty("lastname")
        private String lastName;
        private String email;
        @JsonProperty("datecreated")
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime dateCreated;
        private List<Client> clients;

        public List<Client> getClients() {
            return clients != null ? clients : List.of();
        }


        @Getter
        public static class Client {
            private Long id;
            private Boolean isOwner;
        }
    }
}