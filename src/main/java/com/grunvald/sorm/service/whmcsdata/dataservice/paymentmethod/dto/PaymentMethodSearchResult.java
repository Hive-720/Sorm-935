package com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.dto;

import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;

import java.util.List;


public record PaymentMethodSearchResult(String cursor, boolean hasNextPage, List<PaymentMethod> paymentMethodList) {
}