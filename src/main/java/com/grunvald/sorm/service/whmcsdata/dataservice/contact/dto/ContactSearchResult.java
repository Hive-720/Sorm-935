package com.grunvald.sorm.service.whmcsdata.dataservice.contact.dto;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;

import java.util.List;


public record ContactSearchResult(String cursor, boolean hasNextPage, List<Contact> contactList) {
}