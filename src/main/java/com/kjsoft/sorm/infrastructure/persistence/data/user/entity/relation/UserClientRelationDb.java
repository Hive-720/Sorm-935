package com.kjsoft.sorm.infrastructure.persistence.data.user.entity.relation;

import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class UserClientRelationDb {
    private Long clientId;
    private Long userId;
    private OffsetDateTime createdAt;
}