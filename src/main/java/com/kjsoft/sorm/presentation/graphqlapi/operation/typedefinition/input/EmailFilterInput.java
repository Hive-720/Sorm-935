package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.EmailBaseFilter;

import java.util.List;


public record EmailFilterInput(
        ClientFilterInput client,
        EmailBaseFilter to,
        EmailBaseFilter cc,
        EmailBaseFilter bcc,
        DateTimeBaseFilter date,
        DateTimeBaseFilter createdAt,
        DateTimeBaseFilter updatedAt,
        List<EmailFilterInput> and,
        List<EmailFilterInput> or,
        EmailFilterInput not
) {
}