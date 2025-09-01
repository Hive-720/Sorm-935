package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition;

public record StringRegExpFullTextFilter(String regexp, String alloftext, String anyoftext) {
}