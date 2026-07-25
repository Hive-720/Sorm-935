package com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.dto;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;

import java.util.List;


public record AffiliateSearchResult(String cursor, boolean hasNextPage, List<Affiliate> affiliateList) {
}