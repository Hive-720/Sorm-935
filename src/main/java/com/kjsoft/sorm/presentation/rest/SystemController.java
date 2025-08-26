package com.kjsoft.sorm.presentation.rest;

import com.kjsoft.sorm.business.systemnotification.SystemNotificationService;
import com.kjsoft.sorm.presentation.rest.request.MetricAlertsRequest;
import com.kjsoft.sorm.presentation.rest.request.NotificationRequest;
import com.kjsoft.sorm.presentation.rest.request.mapper.MetricAlertsMapper;
import com.kjsoft.sorm.presentation.rest.request.mapper.SystemNotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;


//TODO перекрыть доступ к /system через прокси ?
@RestController("/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemNotificationService systemNotificationService;

    @PutMapping("/notifications/push")
    @ResponseStatus(OK)
    //TODO валидации!!!
    public Mono<Void> pushNotifications(@Valid @RequestBody List<NotificationRequest> body) {
        return Flux.fromIterable(body)
                .map(SystemNotificationMapper.INSTANCE::toEntity)
                .collectList()
                .flatMap(systemNotificationService::pushAll);
    }

    @PutMapping("/metric/alerts/push")
    @ResponseStatus(OK)
    public Mono<Void> pushAlert(@Valid @RequestBody MetricAlertsRequest body) {
        var metricAlerts = MetricAlertsMapper.INSTANCE.toEntity(body);
        return systemNotificationService.pushMetricAlerts(metricAlerts);
    }
}