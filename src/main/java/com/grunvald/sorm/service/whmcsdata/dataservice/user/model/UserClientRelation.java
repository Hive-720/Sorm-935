package com.grunvald.sorm.service.whmcsdata.dataservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class UserClientRelation {
    private Long userId;
    private Long clientId;
    private OffsetDateTime createdAt;
}