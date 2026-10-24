package com.grunvald.sorm.util;

import org.springframework.lang.Nullable;

import java.util.Currency;
import java.util.Map;
import java.util.stream.Collectors;


public class CurrencyUtil {

    private static final Map<Integer, String> NUMERIC_TO_ALPHA3 = Currency.getAvailableCurrencies()
            .stream()
            // у валют без цифрового кода (XFO, XFU) numericCode == 0, и они дают дубликаты ключей
            .filter(currency -> currency.getNumericCode() > 0)
            .collect(Collectors.toMap(Currency::getNumericCode, Currency::getCurrencyCode, (first, second) -> first));

    @Nullable
    public static String getValidCurrency(String currency) {
        if (currency == null) {
            return null;
        }

        try {
            return Currency.getInstance(currency).getCurrencyCode();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String getCurrency(Integer code) {
        return code == null ? null : NUMERIC_TO_ALPHA3.get(code);
    }
}