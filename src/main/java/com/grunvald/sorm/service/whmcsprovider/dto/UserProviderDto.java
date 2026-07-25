package com.grunvald.sorm.service.whmcsprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
public class UserProviderDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private OffsetDateTime dateCreated;
    private List<Client> clients;


    @Getter
    @AllArgsConstructor
    public static class Client {
        private Long id;
    }
}