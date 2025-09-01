package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer.IpAddressDeserializer;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;


@Setter
public class GetActivityLogResponse extends Response {
    private Activity activity;

    public List<Activity.ActivityLog> getActivities() {
        return activity != null && activity.entry != null ? activity.entry : List.of();
    }

    @Getter
    @Setter
    public static class Activity {
        private List<ActivityLog> entry;


        @Getter
        @Setter
        public static class ActivityLog {
            private Long id;
            private Long clientId;
            private Long userId;
            private Long userid; //userid=clientId
            private Long adminId;
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime date;
            private String description;
            private String username;
            @JsonProperty("ipaddress")
            @JsonDeserialize(using = IpAddressDeserializer.class)
            private String ipAddress;
        }
    }
}