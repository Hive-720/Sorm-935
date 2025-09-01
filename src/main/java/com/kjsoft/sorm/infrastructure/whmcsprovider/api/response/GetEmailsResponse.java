package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsEmailFromStrDeserializer;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsEmailListFromStrDeserializer;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsEmailMsgBodyFromHtmlDeserializer;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
public class GetEmailsResponse extends Response {
    private Emails emails;

    public List<Emails.Email> getEmails() {
        return emails != null && emails.email != null ? emails.email : List.of();
    }

    @Getter
    @Setter
    public static class Emails {
        private List<Email> email;

        @Getter
        @Setter
        public static class Email {
            private Long id;
            @JsonProperty("userid")
            private Long clientId;
            private String subject;
            @JsonProperty("message")
            @JsonDeserialize(using = WhmcsEmailMsgBodyFromHtmlDeserializer.class)
            private String messageBody;
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime date;
            @JsonDeserialize(using = WhmcsEmailFromStrDeserializer.class)
            private String to;
            @JsonDeserialize(using = WhmcsEmailListFromStrDeserializer.class)
            private List<String> cc;
            @JsonDeserialize(using = WhmcsEmailListFromStrDeserializer.class)
            private List<String> bcc;
            private String attachments;
            private Integer pending;
            @JsonProperty("message_data")
            private String messageData;
            private Integer failed;
            @JsonProperty("failure_reason")
            private String failureReason;
            @JsonProperty("retry_count")
            private Integer retryCount;
            @JsonProperty("created_at")
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime createdAt;
            @JsonProperty("updated_at")
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime updatedAt;
            @JsonProperty("campaign_id")
            private Long campaignId;
        }
    }
}