package com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.kjsoft.sorm.util.EqualsUtils.isBigDecimalEqual;
import static com.kjsoft.sorm.util.EqualsUtils.isOffsetDateTimeEqual;


@Getter
@Setter
public class Invoice {
    private Long id;
    private Long clientId;
    private BigDecimal subTotal;
    private BigDecimal total;
    private BigDecimal credit;
    private BigDecimal balance;
    private BigDecimal tax;
    private BigDecimal tax2;
    private BigDecimal taxRate;
    private BigDecimal taxRate2;
    private String status;
    private String currency;
    private String paymentMethod;
    private List<InvoiceItem> items;
    private OffsetDateTime date;
    private OffsetDateTime dueDate;
    private OffsetDateTime datePaid;
    private OffsetDateTime dateRefunded;
    private OffsetDateTime dateCancelled;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;


    public boolean update(Invoice actual) {
        var changed = false;

        if (!isBigDecimalEqual(subTotal, actual.subTotal)) {
            this.subTotal = actual.subTotal;
            changed = true;
        }

        if (!isBigDecimalEqual(total, actual.total)) {
            this.total = actual.total;
            changed = true;
        }

        if (!isBigDecimalEqual(credit, actual.credit)) {
            this.credit = actual.credit;
            changed = true;
        }

        if (!isBigDecimalEqual(balance, actual.balance)) {
            this.balance = actual.balance;
            changed = true;
        }

        if (!isBigDecimalEqual(tax, actual.tax)) {
            this.tax = actual.tax;
            changed = true;
        }

        if (!isBigDecimalEqual(tax2, actual.tax2)) {
            this.tax2 = actual.tax2;
            changed = true;
        }

        if (!isBigDecimalEqual(taxRate, actual.taxRate)) {
            this.taxRate = actual.taxRate;
            changed = true;
        }

        if (!isBigDecimalEqual(taxRate2, actual.taxRate2)) {
            this.taxRate2 = actual.taxRate2;
            changed = true;
        }

        if (!Objects.equals(this.status, actual.status)) {
            this.status = actual.status;
            changed = true;
        }

        if (!Objects.equals(this.currency, actual.currency)) {
            this.currency = actual.currency;
            changed = true;
        }

        if (!Objects.equals(this.paymentMethod, actual.paymentMethod)) {
            this.paymentMethod = actual.paymentMethod;
            changed = true;
        }

        if (!InvoiceItem.areItemsEqual(this.items, actual.items)) {
            this.items = actual.items != null ? actual.items : null;
            changed = true;
        }

        if (!isOffsetDateTimeEqual(this.dueDate, actual.dueDate)) {
            this.dueDate = actual.dueDate;
            changed = true;
        }

        if (!isOffsetDateTimeEqual(this.datePaid, actual.datePaid)) {
            this.datePaid = actual.datePaid;
            changed = true;
        }

        if (!isOffsetDateTimeEqual(this.dateRefunded, actual.dateRefunded)) {
            this.dateRefunded = actual.dateRefunded;
            changed = true;
        }

        if (!isOffsetDateTimeEqual(this.dateCancelled, actual.dateCancelled)) {
            this.dateCancelled = actual.dateCancelled;
            changed = true;
        }

        if (!isOffsetDateTimeEqual(this.updatedAt, actual.updatedAt)) {
            this.updatedAt = actual.updatedAt;
            changed = true;
        }

        return changed;
    }


    @Getter
    @Setter
    public static class InvoiceItem {
        private Long id;
        private Long relId;
        private String type;
        private String description;
        private BigDecimal amount;
        private Boolean taxed;


        private static boolean areItemsEqual(List<InvoiceItem> list1, List<InvoiceItem> list2) {
            if (list1 == list2) return true;
            if (list1 == null || list2 == null) return false;
            if (list1.size() != list2.size()) return false;

            for (var i = 0; i < list1.size(); i++) {
                var item1 = list1.get(i);
                var item2 = list2.get(i);

                if (!isItemEqual(item1, item2)) {
                    return false;
                }
            }
            return true;
        }

        private static boolean isItemEqual(InvoiceItem item1, InvoiceItem item2) {
            if (item1 == item2) return true;
            if (item1 == null || item2 == null) return false;

            return Objects.equals(item1.getId(), item2.getId()) &&
                    Objects.equals(item1.getRelId(), item2.getRelId()) &&
                    Objects.equals(item1.getType(), item2.getType()) &&
                    Objects.equals(item1.getDescription(), item2.getDescription()) &&
                    isBigDecimalEqual(item1.getAmount(), item2.getAmount()) &&
                    Objects.equals(item1.getTaxed(), item2.getTaxed());
        }
    }
}