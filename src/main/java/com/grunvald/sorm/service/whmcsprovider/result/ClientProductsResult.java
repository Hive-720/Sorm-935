package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.ClientProductProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ClientProductsResult {
    private List<ClientProductProviderDto> elements;
    private boolean hasNextPage;
    private Long nextOffset;
}