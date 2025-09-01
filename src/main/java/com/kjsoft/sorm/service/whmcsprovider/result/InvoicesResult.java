package com.kjsoft.sorm.service.whmcsprovider.result;

import com.kjsoft.sorm.service.whmcsprovider.dto.InvoiceProviderDto;
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