package com.kjsoft.sorm.infrastructure.persistance.systemnotificationdb;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


@Builder
@Getter
public class SystemNotificationDB {

    @Id
    private Long id;
    private String uuid;
    private boolean sent;
    private String message;
    private LocalDateTime startAt;
    private String type;
    //TODO MetricAlerts!!! как JSON ?
}