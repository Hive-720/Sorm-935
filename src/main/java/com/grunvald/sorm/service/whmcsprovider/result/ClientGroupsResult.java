package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ClientGroupsResult {
    private List<ClientGroupProviderDto> elements;
    private boolean hashNextPage;
}