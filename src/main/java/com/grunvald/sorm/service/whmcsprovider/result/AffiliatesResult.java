package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.AffiliateProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class AffiliatesResult {
    private List<AffiliateProviderDto> elements;
    private boolean hasNextPage;
    private long nextOffset;
}