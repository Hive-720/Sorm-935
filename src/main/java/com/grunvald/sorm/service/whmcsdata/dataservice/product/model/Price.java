package com.grunvald.sorm.service.whmcsdata.dataservice.product.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.grunvald.sorm.util.EqualsUtils.isBigDecimalEqual;


@Getter
@Setter
public class Price {
    private String currency;
    private BigDecimal mSetupFee;
    private BigDecimal qSetupFee;
    private BigDecimal sSetupFee;
    private BigDecimal aSetupFee;
    private BigDecimal bSetupFee;
    private BigDecimal tSetupFee;
    private BigDecimal monthly;
    private BigDecimal quarterly;
    private BigDecimal semiannually;
    private BigDecimal annually;
    private BigDecimal biennially;
    private BigDecimal triennially;


    private boolean hasDifferences(Price actual) {
        if (actual == null) return true;

        return !isBigDecimalEqual(this.mSetupFee, actual.mSetupFee) ||
                !isBigDecimalEqual(this.qSetupFee, actual.qSetupFee) ||
                !isBigDecimalEqual(this.sSetupFee, actual.sSetupFee) ||
                !isBigDecimalEqual(this.aSetupFee, actual.aSetupFee) ||
                !isBigDecimalEqual(this.bSetupFee, actual.bSetupFee) ||
                !isBigDecimalEqual(this.tSetupFee, actual.tSetupFee) ||
                !isBigDecimalEqual(this.monthly, actual.monthly) ||
                !isBigDecimalEqual(this.quarterly, actual.quarterly) ||
                !isBigDecimalEqual(this.semiannually, actual.semiannually) ||
                !isBigDecimalEqual(this.annually, actual.annually) ||
                !isBigDecimalEqual(this.biennially, actual.biennially) ||
                !isBigDecimalEqual(this.triennially, actual.triennially);
    }


    public static boolean isEqual(List<Price> local, List<Price> actual) {
        if (local == null && actual == null) {
            return true;
        }

        if (actual == null || local == null) {
            return false;
        }

        var currentMap = local.stream()
                .collect(Collectors.toMap(Price::getCurrency, Function.identity(), (a, b) -> a));

        var actualMap = actual.stream()
                .collect(Collectors.toMap(Price::getCurrency, Function.identity(), (a, b) -> a));

        if (!currentMap.keySet().equals(actualMap.keySet())) {
            return false;
        }


        for (var actualPrice : actual) {
            var currentPrice = currentMap.get(actualPrice.getCurrency());

            if (currentPrice.hasDifferences(actualPrice)) {
                return false;
            }
        }

        return true;
    }
}