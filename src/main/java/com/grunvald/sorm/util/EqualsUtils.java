package com.grunvald.sorm.util;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;


public class EqualsUtils {

    public static boolean isBigDecimalEqual(BigDecimal d1, BigDecimal d2) {
        if (d1 == null && d2 == null) return true;
        if (d1 == null || d2 == null) return false;
        return d1.compareTo(d2) == 0;
    }

    public static boolean isOffsetDateTimeEqual(OffsetDateTime odt1, OffsetDateTime odt2) {
        if (odt1 == null && odt2 == null) return true;
        if (odt1 == null || odt2 == null) return false;
        return odt1.isEqual(odt2);
    }

    public static boolean areListsEqual(List<String> list1, List<String> list2) {
        if (list1 == null && list2 == null) return true;
        if (list1 == null || list2 == null) return false;
        if (list1.size() != list2.size()) return false;
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}