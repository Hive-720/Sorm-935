package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class InvoicesResult {
    private List<InvoiceProviderDto> elements;
    private boolean hasNextPage;
    private Long nextOffset;
}