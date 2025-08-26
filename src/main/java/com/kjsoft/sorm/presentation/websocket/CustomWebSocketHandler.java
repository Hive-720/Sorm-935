package com.kjsoft.sorm.presentation.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjsoft.sorm.business.exception.InternalErrorException;
import com.kjsoft.sorm.business.systemnotification.SystemNotificationService;
import com.kjsoft.sorm.presentation.graphql.GraphQLAnalyzer;
import com.kjsoft.sorm.presentation.websocket.error.exception.*;
import com.kjsoft.sorm.presentation.websocket.message.MessageIn;
import com.kjsoft.sorm.presentation.websocket.message.MessageOut;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static com.kjsoft.sorm.presentation.websocket.message.MessageIn.MessageInType.CONNECTION_INIT;
import static com.kjsoft.sorm.presentation.websocket.message.MessageOut.MessageOutType.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomWebSocketHandler implements WebSocketHandler {

    private final SystemNotificationService systemNotificationService;
    private final SubscriptionRegistry subscriptionRegistry;
    private final ObjectMapper objectMapper;
    private final GraphQlSource graphQlSource;
    private final GraphQLAnalyzer analyzer;
    private final AtomicReference<String> sessionId = new AtomicReference<>();

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        return Mono.firstWithSignal(waitConnectionInit(session), messageProcessing(session))
                .doOnTerminate(() -> {
                            log.info("cancel all subscription and remove session"); //TODO
                            subscriptionRegistry.cancelAll();
                            sessionId.set(null);
                        }
                );
    }

    private Mono<Void> waitConnectionInit(WebSocketSession session) {
        return Mono.delay(Duration.ofSeconds(15)) //TODO время из конфига брать
                .handle((tick, sink) -> {
                    if (sessionId.get() == null) {
                        sink.error(new ConnectionTimeoutException());
                    } else {
                        sink.next(tick);
                    }
                })
                .onErrorResume(e -> handleError(e, session))
                .then(Mono.never());
    }

    private Mono<Void> messageProcessing(WebSocketSession session) {
        return session.receive()
                .map(this::parseAndValidateMessage)
                .doOnNext(msgIn -> checkForSession(session, msgIn))
                .flatMap(message ->
                        switch (message.getType()) {
                            //TODO id нужно добавлять сразу, что бы потом не было сюрпризов
                            case SUBSCRIBE -> handleSubscription(session, message);
                            case COMPLETE -> cancelSubscription(session, message.getId());
                            case CONNECTION_INIT -> initSession(session);
                        }
                )
                .onErrorContinue((e, obj) -> handleError(e, session).subscribe())
                .then();
    }

    private MessageIn parseAndValidateMessage(WebSocketMessage webSocketMessage) {
        try {
            var json = webSocketMessage.getPayloadAsText();
            var message = objectMapper.readValue(json, MessageIn.class);
            //TODO validate message
            return message;
        } catch (JsonProcessingException e) {
            throw new InvalidMessageException(e.getMessage());
        }
    }

    private void checkForSession(WebSocketSession session, MessageIn message) {
        var sessionId = this.sessionId.get();
        if (message.getType() != CONNECTION_INIT && sessionId == null || //TODO условия думаю вынести
                (sessionId != null && !sessionId.equals(session.getId()))) {
            throw new UnauthorizedException();
        } else if (message.getType() == CONNECTION_INIT && sessionId != null) {
            throw new SessionAlreadyExistsException();
        }
    }

    private Mono<Void> sendMessage(WebSocketSession session, MessageOut message) {
        try {
            var json = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(message);
            var webSocketMessage = session.textMessage(json);
            return session.send(Mono.just(webSocketMessage));
        } catch (JsonProcessingException e) {
            return Mono.error(new InternalErrorException());//TODO системная ошибка - проблема с моей стороны
        }
    }

    private Mono<Void> initSession(WebSocketSession session) {
        sessionId.set(session.getId());
        return sendMessage(session, new MessageOut(CONNECTION_ASK));
    }

    private Mono<Void> cancelSubscription(WebSocketSession session, String requestId) {
        subscriptionRegistry.cancel(requestId);
        return sendMessage(session, new MessageOut(requestId, COMPLETE));
    }

    //TODO отдавать complete msg
    private Mono<Void> handleSubscription(WebSocketSession session, MessageIn msgIn) {
        return Mono.just(msgIn)
                .doOnNext(this::checkAvailabilitySubscriptionAndAdd)
                .map(msg -> objectMapper.convertValue(msgIn.getPayload(), MessageIn.SubscribePayload.class))
                .handle((payload, sink) -> {
                    var query = payload.getQuery();
                    if (!analyzer.isSubscription(query)) {
                        sink.error(new InvalidMessageException("Subscription only is supported")); //TODO или error ответ?
                    } else {
                        sink.next(payload);
                    }
                })
                .cast(MessageIn.SubscribePayload.class)
                .flatMap(payload ->
                        Mono.fromFuture(graphQlSource.graphQl().executeAsync(
                                        ExecutionInput.newExecutionInput()
                                                .query(payload.getQuery())
                                                .build()
                                )
                        )
                )
                .handle((res, sink) -> {
                    //TODO возможно получится сразу получать error через CustomExceptionResolver
                    if (!res.getErrors().isEmpty()) {
                        //TODO тут отправить ERROR
                        sink.error(new RuntimeException());
                    } else {
                        sink.next(res);
                    }
                })
                .cast(ExecutionResult.class)
                .log()
                .flatMap(result -> {
                    var subscription = Flux.from(result.getData())
                            .log()
                            //TODO вылавливаем ошибки и конвертим в ERROR
                            //todo        var msgOut = new MessageOut("", ERROR, "payload");
                            //todo        return sendMessage(session, msgOut);
                            .subscribe(data -> {
                                // var ext = result.getExtensions();
                                // var notify = ext.get("systemNotification");
                                var msgOut = new MessageOut(msgIn.getId(), NEXT, data);
                                sendMessage(session, msgOut)
                                        //  .flatMap(vo -> systemNotificationService.markAsSent((SystemNotification) notify))
                                        //TODO обработка ошибок??
                                        .subscribe();


                            });

                    subscriptionRegistry.add(msgIn.getId(), subscription);
                    return Mono.empty();
                });
    }

    private void checkAvailabilitySubscriptionAndAdd(MessageIn msgIn) {
        if (subscriptionRegistry.isExist(msgIn.getId())) {
            throw new SubscriptionAlreadyExistsException(msgIn.getId());
        } else {
            //TODO null так то нельзя передавать
            // subscriptionRegistry.add(msgIn.getId(), null);
        }
    }

    private Mono<Void> handleError(Throwable e, WebSocketSession session) {
        log.error("ERROR123:" + e.getMessage());
        var closeStatus = CloseStatus.NO_STATUS_CODE;
        if (e instanceof WebSocketException webSocketException) {
            closeStatus = webSocketException.getCloseStatus();
        }
        System.out.println("CLOSE: " + closeStatus);
        return session.close(closeStatus);
    }
}