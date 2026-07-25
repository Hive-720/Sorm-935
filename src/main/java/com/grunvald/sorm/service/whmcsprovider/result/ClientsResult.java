package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.ClientShortProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ClientsResult {
    private List<ClientShortProviderDto> elements;
    private boolean hasNextPage;
    private long nextOffset;
}