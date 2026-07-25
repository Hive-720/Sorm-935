package com.grunvald.sorm.service.whmcsdata.dataservice.user.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;


@Getter
@Setter
public class User {
    private Long id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private OffsetDateTime dateCreated;


    public boolean update(User actual) {
        var changed = false;

        if (!Objects.equals(this.firstName, actual.firstName)) {
            this.firstName = actual.firstName;
            changed = true;
        }

        if (!Objects.equals(this.lastName, actual.lastName)) {
            this.lastName = actual.lastName;
            changed = true;
        }

        if (!Objects.equals(this.fullName, actual.fullName)) {
            this.fullName = actual.fullName;
            changed = true;
        }

        if (!Objects.equals(this.email, actual.email)) {
            this.email = actual.email;
            changed = true;
        }

        return changed;
    }
}