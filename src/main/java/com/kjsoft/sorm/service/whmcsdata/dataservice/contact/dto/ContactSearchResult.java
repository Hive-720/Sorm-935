package com.kjsoft.sorm.service.whmcsdata.dataservice.contact.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.contact.model.Contact;

import java.util.List;


public record ContactSearchResult(String cursor, boolean hasNextPage, List<Contact> contactList) {
}