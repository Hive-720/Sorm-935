package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.math.BigDecimal;
//import java.time.OffsetDateTime;
//import java.util.List;
//
//
//@Getter
//@Setter
//public class GetTransactionsResponse extends Response {
//    private Transactions transactions;
//
//    public List<Transactions.Transaction> getTransactions() {
//        return transactions.transaction;
//    }
//
//
//    @Getter
//    @Setter
//    public static class Transactions {
//        private List<Transaction> transaction;
//
//
//        @Getter
//        @Setter
//        public static class Transaction {
//            private Long id;
//            @JsonProperty("userid")
//            private Long clientId;
//            private String currency;
//            private String gateway;
//            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
//            private OffsetDateTime date;
//            private String description;
//            @JsonProperty("amountin")
//            private BigDecimal amountIn;
//            private BigDecimal fees;
//            @JsonProperty("amountout")
//            private BigDecimal amountOut;
//            private BigDecimal rate;
//            @JsonProperty("transid")
//            private String transId;
//            @JsonProperty("invoiceid")
//            private Long invoiceId;
//            @JsonProperty("refundid")
//            private Long refundId;
//        }
//    }
//}