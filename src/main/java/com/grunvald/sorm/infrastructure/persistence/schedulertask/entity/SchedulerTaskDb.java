package com.grunvald.sorm.infrastructure.persistence.schedulertask.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class SchedulerTaskDb {
    private Long id;
    private String schedulerType;
    private String cursor;
    private long offset;
    private String status;
    private OffsetDateTime startAt;
    private OffsetDateTime finishAt;
}