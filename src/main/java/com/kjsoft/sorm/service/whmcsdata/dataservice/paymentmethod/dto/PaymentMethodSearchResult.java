package com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;

import java.util.List;


public record PaymentMethodSearchResult(String cursor, boolean hasNextPage, List<PaymentMethod> paymentMethodList) {
}