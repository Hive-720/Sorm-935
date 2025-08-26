package com.kjsoft.sorm.presentation.rest.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
//TODO валидации
public class MetricAlertsRequest {
    private String groupKey;
    private String status;
    private String receiver;
    private List<KeyValueRef> groupLabels;
    private List<KeyValueRef> commonLabels;
    private List<KeyValueRef> commonAnnotations;
    private String externalURL;
    private List<AlertRequest> alerts;

    @Getter
    @Builder
    public static class AlertRequest {
        private String alertStatus;
        private List<KeyValueRef> labels;
        private List<KeyValueRef> annotations;
        //TODO   #YYYY-MM-DDThh:mm:ssthh:mm (в UTC)
        private LocalDateTime startAt;
        private LocalDateTime endsAt;
        private String generatorURL;
        private String fingerprint;
    }

    @Getter
    @Builder
    public static class KeyValueRef {
        private String key;
        private String value;
    }
}