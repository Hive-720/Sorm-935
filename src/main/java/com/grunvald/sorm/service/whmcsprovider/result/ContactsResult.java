package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.ContactProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ContactsResult {
    private List<ContactProviderDto> elements;
    private boolean hasNextPage;
    private Long nextOffset;
}