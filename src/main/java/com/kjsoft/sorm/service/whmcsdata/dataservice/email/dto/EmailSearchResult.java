package com.kjsoft.sorm.service.whmcsdata.dataservice.email.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.email.model.Email;

import java.util.List;


public record EmailSearchResult(String cursor, boolean hasNextPage, List<Email> emailList) {
}