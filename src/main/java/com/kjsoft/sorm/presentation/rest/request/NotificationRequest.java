package com.kjsoft.sorm.presentation.rest.request;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class NotificationRequest {
    private String type;
    private String message;
    private LocalDateTime startAt; //TODO или long?
}