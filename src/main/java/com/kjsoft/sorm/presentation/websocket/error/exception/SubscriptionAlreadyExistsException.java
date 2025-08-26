package com.kjsoft.sorm.presentation.websocket.error.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.socket.CloseStatus;

import static com.kjsoft.sorm.presentation.websocket.error.CloseSessionStatus.SUBSCRIPTION_ALREADY_EXISTS;


@RequiredArgsConstructor
public class SubscriptionAlreadyExistsException extends WebSocketException {

    private final String operationId;

    @Override
    public CloseStatus getCloseStatus() {
        var message = "Subscriber for %s already exists".formatted(operationId);
        return SUBSCRIPTION_ALREADY_EXISTS.toCloseStatus(message);
    }
}