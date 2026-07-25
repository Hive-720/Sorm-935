package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition;

import java.util.List;


public record StringTermExactFilter(String allofterms, String anyofterms, String eq, List<String> in) {
}