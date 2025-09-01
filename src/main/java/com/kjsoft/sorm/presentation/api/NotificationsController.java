package com.kjsoft.sorm.presentation.api;

import com.kjsoft.sorm.presentation.config.RestApiConfig;
import com.kjsoft.sorm.service.system.notification.SystemNotificationService;
import com.kjsoft.sorm.presentation.api.error.exception.TooManyRequestsException;
import com.kjsoft.sorm.presentation.api.request.MetricAlertsRequest;
import com.kjsoft.sorm.presentation.api.request.NotificationRequest;
import com.kjsoft.sorm.presentation.api.request.mapper.MetricAlertsPresentationMapper;
import com.kjsoft.sorm.presentation.api.request.mapper.SystemNotificationPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequiredArgsConstructor
public class NotificationsController {
    private final SystemNotificationService systemNotificationService;
    private final SystemNotificationPresentationMapper notifyMapper;
    private final MetricAlertsPresentationMapper metricsAlertMapper;
    private final RestApiConfig restApiConfig;
    //TODO: рассмотреть другие алгоритмы
    private final AtomicInteger activeRequests = new AtomicInteger(0);
    private final AtomicInteger activeAlertRequests = new AtomicInteger(0);


    @PutMapping("/notifications/push")
    @ResponseStatus(OK)
    public Mono<Void> pushNotifications(@Valid @RequestBody NotificationRequest body) {
        return Mono.defer(() -> {
                    var apiConfig = restApiConfig.getApiConfig(RestApiConfig.Api.PUSH);
                    if (activeRequests.get() >= apiConfig.getMaxRequests()) {
                        return Mono.error(new TooManyRequestsException("Too many concurrent requests"));
                    }
                    activeRequests.incrementAndGet();
                    return Mono.just(body);
                })
                .map(notifyMapper::map)
                .flatMap(systemNotificationService::push)
                .doFinally(signal -> activeRequests.decrementAndGet());
    }

    @PutMapping("/notifications/metric/alerts/push")
    @ResponseStatus(OK)
    public Mono<Void> pushAlert(@Valid @RequestBody MetricAlertsRequest body) {
        var apiConfig = restApiConfig.getApiConfig(RestApiConfig.Api.ALERT);
        if (activeRequests.get() >= apiConfig.getMaxRequests()) {
            return Mono.error(new TooManyRequestsException("Too many concurrent requests"));
        }

        activeAlertRequests.incrementAndGet();
        var metricAlerts = metricsAlertMapper.map(body);
        return systemNotificationService.pushMetricAlerts(metricAlerts)
                .doFinally(signal -> activeRequests.decrementAndGet());
    }
}