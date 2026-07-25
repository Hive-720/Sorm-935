package com.grunvald.sorm.infrastructure.persistence.data.user.entity;

import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class UserDb {
    private Long id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private OffsetDateTime dateCreated;
}