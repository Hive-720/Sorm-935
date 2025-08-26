package com.kjsoft.sorm.presentation.websocket.error.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.socket.CloseStatus;

import static com.kjsoft.sorm.presentation.websocket.error.CloseSessionStatus.INVALID_MESSAGE;


@RequiredArgsConstructor
public class InvalidMessageException extends WebSocketException {

    private final String message;

    @Override
    public CloseStatus getCloseStatus() {
        return INVALID_MESSAGE.toCloseStatus(message);
    }
}