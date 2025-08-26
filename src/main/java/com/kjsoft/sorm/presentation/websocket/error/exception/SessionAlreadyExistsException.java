package com.kjsoft.sorm.presentation.websocket.error.exception;

import org.springframework.web.reactive.socket.CloseStatus;

import static com.kjsoft.sorm.presentation.websocket.error.CloseSessionStatus.SESSION_ALREADY_EXISTS;


public class SessionAlreadyExistsException extends WebSocketException {

    @Override
    public CloseStatus getCloseStatus() {
        return SESSION_ALREADY_EXISTS.toCloseStatus();
    }
}