package com.kjsoft.sorm.presentation.graphql.query.result;

import lombok.Data;

import java.util.UUID;


@Data
public class QueryMessage {
    private String requestID = UUID.randomUUID().toString();
    private boolean offline = false;
    private Integer maxPageSize = 100;
}