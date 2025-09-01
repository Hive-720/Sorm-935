package com.kjsoft.sorm.presentation.api.error;

import com.kjsoft.sorm.config.NotificationPropertiesConfig;
import com.kjsoft.sorm.presentation.api.error.exception.TooManyRequestsException;
import com.kjsoft.sorm.service.system.notification.SystemNotificationService;
import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import com.kjsoft.sorm.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static com.kjsoft.sorm.presentation.api.error.ErrorCode.*;
import static com.kjsoft.sorm.service.system.notification.model.Type.MINORERROR;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {
    private final MessageSource messageSource;
    private final NotificationPropertiesConfig notificationConfig;
    private final SystemNotificationService systemNotificationService;
    private final DateUtils date;


    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(MethodNotAllowedException e, Locale locale) {
        log.debug(e.getMessage());
        return buildErrorMessage(METHOD_NOT_ALLOWED, null, locale);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorMessage> resourceNotFound(NoResourceFoundException e, Locale locale) {
        log.debug(e.getMessage());
        return buildErrorMessage(RESOURCE_NOT_FOUND, null, locale);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleJsonReadError(HttpMessageNotReadableException e, Locale locale) {
        log.debug(e.getMessage());
        return buildErrorMessage(INVALID_REQUEST, null, locale);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorMessage> handleTooManyRequests(TooManyRequestsException e, Locale locale) {
        log.debug(e.getMessage());
        return buildErrorMessage(TOO_MANY_REQUESTS, null, locale);
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ErrorMessage>> internalError(Throwable e, Locale locale) {
        log.error("Unexpected error", e);
        var errorMsg = buildErrorMessage(INTERNAL_ERROR, null, locale);
        return !notificationConfig.isErrorsApi() ? Mono.empty() :
                systemNotificationService.push(buildNotification())
                        .thenReturn(errorMsg)
                        .doOnError(ex -> log.error("An unexpected error occurred when sending the notify", ex))
                        .onErrorResume(ex -> Mono.just(errorMsg));
    }

    private SystemNotification buildNotification() {
        return SystemNotification.builder()
                .message("Ошибка при взаимодействии с внутренним API SORM")
                .type(MINORERROR)
                .createdAt(date.now())
                .build();
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorMessage> handleInternalException(WebExchangeBindException e, Locale locale) {
        log.debug(e.getMessage());
        Map<String, String> details = null;
        var fieldError = e.getBindingResult().getFieldError();
        if (fieldError != null) {
            details = Collections.singletonMap("param", fieldError.getField());
        }
        return buildErrorMessage(INVALID_PARAMETER, details, locale);
    }

    @ExceptionHandler({InvalidMediaTypeException.class, ServerWebInputException.class})
    public ResponseEntity<ErrorMessage> handleInvalidMediaTypeException(Exception e, Locale locale) {
        log.debug(e.getMessage());
        return buildErrorMessage(INVALID_REQUEST, null, locale);
    }

    @ExceptionHandler(DataBufferLimitException.class)
    public ResponseEntity<ErrorMessage> handleDataBufferLimitException(DataBufferLimitException e, Locale locale) {
        log.debug(e.getMessage());
        return buildErrorMessage(MESSAGE_SIZE_EXCEED, null, locale);
    }

    private ResponseEntity<ErrorMessage> buildErrorMessage(ErrorCode errorCode, Map<String, String> details,
                                                           Locale locale) {
        var msg = messageSource.getMessage(errorCode.name(), null, locale);
        var message = new ErrorMessage(errorCode, msg, details);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(message);
    }
}