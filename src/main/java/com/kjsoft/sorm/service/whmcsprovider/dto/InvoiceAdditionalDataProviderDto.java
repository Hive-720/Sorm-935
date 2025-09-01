package com.kjsoft.sorm.service.whmcsprovider.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Builder
public class InvoiceAdditionalDataProviderDto {
    private Long id;
    private BigDecimal balance;
    private List<Item> items;
    private List<TransactionDto> transactions;

    @Getter
    @Setter
    public static class Item {
        private Long id;
        private String type;
        private Long relId;
        private String description;
        private BigDecimal amount;
        private Boolean taxed;
    }

    @Getter
    @Setter
    public static class TransactionDto {
        private Long id;
        private Long clientId;
        private Long invoiceId;
        private Long refundId;
        private String transId;
        private String gateway;
        private String description;
        private BigDecimal amountIn;
        private BigDecimal fees;
        private BigDecimal amountOut;
        private BigDecimal rate;
        private OffsetDateTime date;
    }
}