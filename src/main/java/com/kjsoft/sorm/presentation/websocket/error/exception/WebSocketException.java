package com.kjsoft.sorm.presentation.websocket.error.exception;

import org.springframework.web.reactive.socket.CloseStatus;


public abstract class WebSocketException extends RuntimeException {

    public abstract CloseStatus getCloseStatus();
}