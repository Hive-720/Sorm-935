package com.kjsoft.sorm.presentation.websocket.error.exception;

import org.springframework.web.reactive.socket.CloseStatus;

import static com.kjsoft.sorm.presentation.websocket.error.CloseSessionStatus.CONNECTION_TIMEOUT;

public class ConnectionTimeoutException extends WebSocketException {

    @Override
    public CloseStatus getCloseStatus() {
        return CONNECTION_TIMEOUT.toCloseStatus();
    }
}