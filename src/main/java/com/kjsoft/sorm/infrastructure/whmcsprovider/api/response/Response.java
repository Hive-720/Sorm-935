package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public abstract class Response {
    @JsonProperty("result")
    @JsonAlias("error")
    protected String status;
    protected String message;
    @JsonProperty("totalresults")
    protected Long totalCount;
    @JsonProperty("startnumber")
    protected Long offset;
    @JsonProperty("numreturned")
    protected Integer limit;
}