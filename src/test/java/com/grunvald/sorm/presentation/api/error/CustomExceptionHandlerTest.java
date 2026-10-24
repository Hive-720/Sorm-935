package com.grunvald.sorm.presentation.api.error;

import com.grunvald.sorm.config.NotificationPropertiesConfig;
import com.grunvald.sorm.presentation.api.error.exception.TooManyRequestsException;
import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.MethodNotAllowedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;
    @Mock
    private NotificationPropertiesConfig notificationConfig;
    @Mock
    private SystemNotificationService notificationService;
    @Mock
    private DateUtils date;

    private CustomExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CustomExceptionHandler(messageSource, notificationConfig, notificationService, date);
        lenient().when(messageSource.getMessage(any(), any(), any(Locale.class))).thenReturn("message");
    }

    @Test
    void methodNotAllowed_returns405() {
        var response = handler.handleNotFound(
                new MethodNotAllowedException("PUT", List.of()), Locale.ENGLISH);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals(ErrorCode.METHOD_NOT_ALLOWED.name(), response.getBody().getError());
    }

    @Test
    void tooManyRequests_returns429() {
        ResponseEntity<ErrorMessage> response =
                handler.handleTooManyRequests(new TooManyRequestsException("limit"), Locale.ENGLISH);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertEquals(ErrorCode.TOO_MANY_REQUESTS.name(), response.getBody().getError());
    }

    @Test
    void internalError_notificationDisabled_noPush() {
        when(notificationConfig.isErrorsApi()).thenReturn(false);

        StepVerifier.create(handler.internalError(new RuntimeException("boom"), Locale.ENGLISH))
                .verifyComplete();

        verify(notificationService, never()).push(any());
    }

    @Test
    void internalError_notificationEnabled_pushesAndReturns() {
        when(notificationConfig.isErrorsApi()).thenReturn(true);
        when(notificationService.push(any())).thenReturn(Mono.empty());

        StepVerifier.create(handler.internalError(new RuntimeException("boom"), Locale.ENGLISH))
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()))
                .verifyComplete();

        verify(notificationService).push(any());
    }

    @Test
    void internalError_pushFails_stillReturnsResponse() {
        when(notificationConfig.isErrorsApi()).thenReturn(true);
        when(notificationService.push(any())).thenReturn(Mono.error(new RuntimeException("notify failed")));

        StepVerifier.create(handler.internalError(new RuntimeException("boom"), Locale.ENGLISH))
                .assertNext(response -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()))
                .verifyComplete();
    }
}
