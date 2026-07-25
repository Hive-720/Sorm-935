package com.grunvald.sorm.service.whmcsdata.dataservice.client.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.grunvald.sorm.util.EqualsUtils.*;


@Getter
@Setter
public class ClientProduct {
    private Long id;
    private Long clientId;
    private Long productId;
    private Long orderId;
    private String name;
    private String domain;
    private String dedicatedIpAddress;
    private BigDecimal firstPaymentAmount;
    private BigDecimal recurringAmount;
    private String billingCycle;
    private String status;
    private List<String> assignedIpAddresses;
    private List<ConfigValue> configs;
    private OffsetDateTime regDate;
    private OffsetDateTime nextDueDate;
    private OffsetDateTime lastUpdate;


    public boolean update(ClientProduct actual) {
        if (actual == null) {
            return false;
        }

        boolean changed = false;

        if (!Objects.equals(this.name, actual.name)) {
            this.name = actual.name;
            changed = true;
        }

        if (!Objects.equals(this.domain, actual.domain)) {
            this.domain = actual.domain;
            changed = true;
        }

        if (!Objects.equals(this.dedicatedIpAddress, actual.dedicatedIpAddress)) {
            this.dedicatedIpAddress = actual.dedicatedIpAddress;
            changed = true;
        }

        if (!isBigDecimalEqual(this.firstPaymentAmount, actual.firstPaymentAmount)) {
            this.firstPaymentAmount = actual.firstPaymentAmount;
            changed = true;
        }

        if (!isBigDecimalEqual(this.recurringAmount, actual.recurringAmount)) {
            this.recurringAmount = actual.recurringAmount;
            changed = true;
        }

        if (!Objects.equals(this.billingCycle, actual.billingCycle)) {
            this.billingCycle = actual.billingCycle;
            changed = true;
        }

        if (!Objects.equals(this.status, actual.status)) {
            this.status = actual.status;
            changed = true;
        }

        if (!areListsEqual(this.assignedIpAddresses, actual.assignedIpAddresses)) {
            this.assignedIpAddresses = actual.assignedIpAddresses;
            changed = true;
        }

        if (!ConfigValue.areConfigValuesEqual(this.configs, actual.configs)) {
            this.configs = actual.configs;
            changed = true;
        }

        if (!isOffsetDateTimeEqual(this.nextDueDate, actual.nextDueDate)) {
            this.nextDueDate = actual.nextDueDate;
            changed = true;
        }

        if (!isOffsetDateTimeEqual(this.lastUpdate, actual.lastUpdate)) {
            this.lastUpdate = actual.lastUpdate;
            changed = true;
        }

        return changed;
    }


    @Getter
    @Setter
    public static class ConfigValue {
        private Long id;
        private String option;
        private String type;
        private String value;


        private static boolean areConfigValuesEqual(List<ConfigValue> list1, List<ConfigValue> list2) {
            if (list1 == null && list2 == null) return true;
            if (list1 == null || list2 == null) return false;
            if (list1.size() != list2.size()) return false;

            for (int i = 0; i < list1.size(); i++) {
                if (!isConfigValueEqual(list1.get(i), list2.get(i))) {
                    return false;
                }
            }
            return true;
        }

        private static boolean isConfigValueEqual(ConfigValue cv1, ConfigValue cv2) {
            if (cv1 == null && cv2 == null) return true;
            if (cv1 == null || cv2 == null) return false;
            return Objects.equals(cv1.getId(), cv2.getId()) &&
                    Objects.equals(cv1.getOption(), cv2.getOption()) &&
                    Objects.equals(cv1.getType(), cv2.getType()) &&
                    Objects.equals(cv1.getValue(), cv2.getValue());
        }
    }
}