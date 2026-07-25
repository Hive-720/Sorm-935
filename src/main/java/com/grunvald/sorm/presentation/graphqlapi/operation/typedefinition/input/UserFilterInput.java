package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.EmailBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.PersonBaseFilter;

import java.util.List;


public record UserFilterInput(
        PersonBaseFilter name,
        EmailBaseFilter email,
        List<UserFilterInput> and,
        List<UserFilterInput> or,
        UserFilterInput not
) {
}