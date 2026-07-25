package com.grunvald.sorm.presentation.api.error;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
public class ErrorMessage {
    private String error;
    private String message;
    private Map<String, String> details;

    public ErrorMessage(ErrorCode errorCode, String message, Map<String, String> details) {
        this.error = errorCode.name();
        this.message = message;
        this.details = details;
    }
}