package com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;

import java.util.List;


public record AffiliateSearchResult(String cursor, boolean hasNextPage, List<Affiliate> affiliateList) {
}