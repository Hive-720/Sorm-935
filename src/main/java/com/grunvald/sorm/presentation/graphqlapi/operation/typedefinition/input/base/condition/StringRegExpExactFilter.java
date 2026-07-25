package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition;

import java.util.List;


public record StringRegExpExactFilter(String regexp, String eq, List<String> in) {
}