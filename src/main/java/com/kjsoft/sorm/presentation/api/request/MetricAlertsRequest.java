package com.kjsoft.sorm.presentation.api.request;

import com.kjsoft.sorm.presentation.api.util.ValueOfEnum;
import com.kjsoft.sorm.service.system.notification.model.MetricAlerts;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

import java.time.OffsetDateTime;
import java.util.List;

//TODO нужно сделать локали под сообщения
@Getter
public class MetricAlertsRequest {
    @NotBlank(message = "groupKey cannot be blank")
    private String groupKey;
    @NotNull
    @ValueOfEnum(enumClass = MetricAlerts.Status.class, ignoreCase = true)
    private String status;
    private String receiver;
    private List<@Valid KeyValueRef> groupLabels;
    private List<@Valid KeyValueRef> commonLabels;
    private List<@Valid KeyValueRef> commonAnnotations;
    @URL(message = "externalURL must be a valid URL")
    private String externalURL;
    @NotNull(message = "alerts cannot be null")
    @Size(min = 1, message = "alerts must contain at least one alert")
    private List<@Valid AlertRequest> alerts;


    @Getter
    public static class AlertRequest {
        @NotNull
        @ValueOfEnum(enumClass = MetricAlerts.Status.class, ignoreCase = true)
        private String alertStatus;
        private List<@Valid KeyValueRef> labels;
        private List<@Valid KeyValueRef> annotations;
        @NotNull
        private OffsetDateTime startAt;
        private OffsetDateTime endsAt;
        @URL(message = "externalURL must be a valid URL")
        private String generatorURL;
        private String fingerprint;
    }

    @Getter
    public static class KeyValueRef {
        @NotBlank(message = "key cannot be blank")
        private String key;
        @NotBlank(message = "value cannot be blank")
        private String value;
    }
}