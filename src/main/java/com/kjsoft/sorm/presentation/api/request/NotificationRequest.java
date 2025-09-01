package com.kjsoft.sorm.presentation.api.request;

import com.kjsoft.sorm.presentation.api.util.ValueOfEnum;
import com.kjsoft.sorm.service.system.notification.model.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.OffsetDateTime;


@Getter
public class NotificationRequest {
    @ValueOfEnum(enumClass = Type.class)
    private String type;
    @NotBlank(message = "Message cannot be blank")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;
    @NotNull(message = "Start date cannot be null")
    private OffsetDateTime startAt;
}