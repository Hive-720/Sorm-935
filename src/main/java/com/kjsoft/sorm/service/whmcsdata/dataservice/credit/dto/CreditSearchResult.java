package com.kjsoft.sorm.service.whmcsdata.dataservice.credit.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.model.Credit;

import java.util.List;


public record CreditSearchResult(String cursor, boolean hasNextPage, List<Credit> creditList) {
}