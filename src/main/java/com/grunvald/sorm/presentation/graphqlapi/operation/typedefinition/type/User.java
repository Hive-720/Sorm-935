package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.EmailBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;

import java.util.List;


public record User(
        Long id,
        PersonBase name,
        EmailBase email,
        List<Client> clients,
        DateTimeBase dateCreated
) {
}