package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type;

import java.math.BigDecimal;


public record Price(
        String currency,
        BigDecimal mSetupFee,
        BigDecimal qSetupFee,
        BigDecimal sSetupFee,
        BigDecimal aSetupFee,
        BigDecimal bSetupFee,
        BigDecimal tSetupFee,
        BigDecimal monthly,
        BigDecimal quarterly,
        BigDecimal semiannually,
        BigDecimal annually,
        BigDecimal biennially,
        BigDecimal triennially
) {
}