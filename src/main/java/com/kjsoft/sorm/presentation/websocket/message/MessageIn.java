package com.kjsoft.sorm.presentation.websocket.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class MessageIn {
    @JsonProperty
    private String id;
    @JsonProperty
    private MessageInType type;
    @JsonProperty
    private Object payload; //TODO String, null, SubscribePayload

    public enum MessageInType {
        @JsonProperty("connection_init")
        CONNECTION_INIT,
        @JsonProperty("subscribe")
        SUBSCRIBE,
        @JsonProperty("complete")
        COMPLETE
    }

    //Запрос в рамках subscribe
    @Getter
    public static class SubscribePayload {
        @JsonProperty
        private String operationName;
        @JsonProperty
        private String query;
        @JsonProperty
        private String variables;
        @JsonProperty
        private String extension;
    }
}