package com.grunvald.sorm.service.whmcsdata.dataservice.credit.dto;

import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;

import java.util.List;


public record CreditSearchResult(String cursor, boolean hasNextPage, List<Credit> creditList) {
}