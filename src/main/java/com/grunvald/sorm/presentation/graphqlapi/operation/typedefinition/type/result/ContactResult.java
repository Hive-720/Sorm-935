package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Contact;

import java.util.List;


public record ContactResult(String cursor, Boolean hasNextPage, List<Contact> result) {
}