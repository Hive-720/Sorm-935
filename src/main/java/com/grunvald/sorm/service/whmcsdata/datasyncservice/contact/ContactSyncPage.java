package com.grunvald.sorm.service.whmcsdata.datasyncservice.contact;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ContactSyncPage {
    private List<Contact> elements;
    private boolean hasNextPage;
    private Long nextOffset;
}