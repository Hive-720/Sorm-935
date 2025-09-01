package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.EmailBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;

import java.util.List;


public record User(
        Long id,
        PersonBase name,
        EmailBase email,
        List<Client> clients,
        DateTimeBase dateCreated
) {
}