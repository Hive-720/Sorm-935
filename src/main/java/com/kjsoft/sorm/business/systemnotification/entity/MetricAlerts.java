package com.kjsoft.sorm.business.systemnotification.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
public class MetricAlerts {
    private String groupKey;
    private Status status;
    private String receiver;
    private List<KeyValueRef> groupLabels;
    private List<KeyValueRef> commonLabels;
    private List<KeyValueRef> commonAnnotations;
    private String externalURL;
    private List<Alert> alerts;

    @Getter
    @Builder
    public static class Alert {
        private Status alertStatus;
        private List<KeyValueRef> labels;
        private List<KeyValueRef> annotations;
        //TODO   #YYYY-MM-DDThh:mm:ssthh:mm (в UTC)
        private LocalDateTime startAt;
        private LocalDateTime endsAt;
        private String generatorURL;
        private String fingerprint;
    }

    public enum Status {
        RESOLVED,
        FIRING
    }

    @Getter
    @Builder
    public static class KeyValueRef {
        private String key;
        private String value;
    }
}