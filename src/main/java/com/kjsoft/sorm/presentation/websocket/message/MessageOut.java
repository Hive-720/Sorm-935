package com.kjsoft.sorm.presentation.websocket.message;

import com.fasterxml.jackson.annotation.JsonProperty;


public class MessageOut {
    @JsonProperty
    private String id;
    @JsonProperty
    private MessageOutType type;
    @JsonProperty
    private Object payload; //TODO String, null, ExecutionResult, GraphQLError

    public MessageOut(MessageOutType type) {
        this.type = type;
    }

    public MessageOut(String id, MessageOutType type) {
        this(type);
        this.id = id;
    }

    public MessageOut(String id, MessageOutType type, Object payload) {
        this(id, type);
        this.payload = payload;
    }

    public enum MessageOutType {
        @JsonProperty("connection_ack")
        CONNECTION_ASK,
        @JsonProperty("next")
        NEXT,
        @JsonProperty("error")
        ERROR,
        @JsonProperty("complete")
        COMPLETE
    }
}