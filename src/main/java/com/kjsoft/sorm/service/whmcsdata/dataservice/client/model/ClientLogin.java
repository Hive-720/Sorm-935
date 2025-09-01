package com.kjsoft.sorm.service.whmcsdata.dataservice.client.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class ClientLogin {
    private Long id;
    private Long clientId;
    private String ipAddress;
    private OffsetDateTime date;

    public ClientLogin(Long clientId, String ipAddress, OffsetDateTime date) {
        this.clientId = clientId;
        this.ipAddress = ipAddress;
        this.date = date;
    }
}