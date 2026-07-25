package com.grunvald.sorm.presentation.api;

import com.grunvald.sorm.presentation.config.RestApiConfig;
import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.presentation.api.error.exception.TooManyRequestsException;
import com.grunvald.sorm.presentation.api.request.MetricAlertsRequest;
import com.grunvald.sorm.presentation.api.request.NotificationRequest;
import com.grunvald.sorm.presentation.api.request.mapper.MetricAlertsPresentationMapper;
import com.grunvald.sorm.presentation.api.request.mapper.SystemNotificationPresentationMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;

import java.util.concurrent.Semaphore;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequiredArgsConstructor
public class NotificationsController {
    private final SystemNotificationService systemNotificationService;
    private final SystemNotificationPresentationMapper notifyMapper;
    private final MetricAlertsPresentationMapper metricsAlertMapper;
    private final RestApiConfig restApiConfig;

    private Semaphore pushSemaphore;
    private Semaphore alertSemaphore;

    @PostConstruct
    void initSemaphores() {
        pushSemaphore = new Semaphore(restApiConfig.getApiConfig(RestApiConfig.Api.PUSH).getMaxRequests());
        alertSemaphore = new Semaphore(restApiConfig.getApiConfig(RestApiConfig.Api.ALERT).getMaxRequests());
    }


    @PutMapping("/notifications/push")
    @ResponseStatus(OK)
    public Mono<Void> pushNotifications(@Valid @RequestBody NotificationRequest body) {
        return Mono.fromCallable(pushSemaphore::tryAcquire)
                .filter(acquired -> acquired)
                .switchIfEmpty(Mono.error(new TooManyRequestsException("Too many concurrent requests")))
                .then(Mono.just(body))
                .map(notifyMapper::map)
                .flatMap(systemNotificationService::push)
                .doFinally(signal -> pushSemaphore.release());
    }

    @PutMapping("/notifications/metric/alerts/push")
    @ResponseStatus(OK)
    public Mono<Void> pushAlert(@Valid @RequestBody MetricAlertsRequest body) {
        return Mono.fromCallable(alertSemaphore::tryAcquire)
                .filter(acquired -> acquired)
                .switchIfEmpty(Mono.error(new TooManyRequestsException("Too many concurrent requests")))
                .then(Mono.fromCallable(() -> metricsAlertMapper.map(body)))
                .flatMap(systemNotificationService::pushMetricAlerts)
                .doFinally(signal -> alertSemaphore.release());
    }
}