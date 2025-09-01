package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.EmailBase;

import java.util.List;


public record Email(Long id,
                    Long clientId,
                    String subject,
                    String messageBody,
                    EmailBase to,
                    List<EmailBase> cc,
                    List<EmailBase> bcc,
                    DateTimeBase date,
                    DateTimeBase createdAt,
                    DateTimeBase updatedAt
) {
}