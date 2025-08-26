package com.kjsoft.sorm.presentation.websocket.error;

import lombok.Getter;
import org.springframework.web.reactive.socket.CloseStatus;


@Getter
public enum CloseSessionStatus {
    UNAUTHORIZED(4401, "Unauthorized"),
    CONNECTION_TIMEOUT(4408, "Connection initialization timeout"),
    SUBSCRIPTION_ALREADY_EXISTS(4409),
    INVALID_MESSAGE(4400),
    SESSION_ALREADY_EXISTS(4429, "To many initialization requests");

    CloseSessionStatus(int code) {
        this.code = code;
    }

    CloseSessionStatus(int code, String message) {
        this(code);
        this.message = message;
    }

    private final int code;
    private String message;

    public CloseStatus toCloseStatus() {
        return CloseStatus.create(code, message);
    }

    public CloseStatus toCloseStatus(String message) {
        return CloseStatus.create(code, message);
    }
}