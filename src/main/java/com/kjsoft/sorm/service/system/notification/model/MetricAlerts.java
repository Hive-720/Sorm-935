package com.kjsoft.sorm.service.system.notification.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
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
    @Setter
    public static class Alert {
        private Status alertStatus;
        private List<KeyValueRef> labels;
        private List<KeyValueRef> annotations;
        private OffsetDateTime startAt;
        private OffsetDateTime endsAt;
        private String generatorURL;
        private String fingerprint;
    }

    public enum Status {
        RESOLVED,
        FIRING
    }

    @Getter
    @Setter
    public static class KeyValueRef {
        private String key;
        private String value;
    }
}