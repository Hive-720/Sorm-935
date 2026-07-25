package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.EmailProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class EmailsResult {
    private List<EmailProviderDto> elements;
    private boolean hasNextPage;
    private Long nextOffset;
}