package com.kjsoft.sorm.presentation.websocket.error.exception;

import org.springframework.web.reactive.socket.CloseStatus;

import static com.kjsoft.sorm.presentation.websocket.error.CloseSessionStatus.UNAUTHORIZED;


public class UnauthorizedException extends WebSocketException {
    @Override
    public CloseStatus getCloseStatus() {
        return UNAUTHORIZED.toCloseStatus();
    }
}