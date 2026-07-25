package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition;

public record StringTermFullTextFilter(String allofterms, String anyofterms, String alloftext, String anyoftext) {
}