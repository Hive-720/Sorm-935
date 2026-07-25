package com.grunvald.sorm.util;

import org.springframework.lang.Nullable;

import static graphql.com.google.common.base.Strings.nullToEmpty;


public final class StringUtils {

    private static final String EMPTY_SEPARATOR = "";

    private StringUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }


    @Nullable
    public static String concatStrings(@Nullable String v1, @Nullable String v2, @Nullable String separator) {
        v1 = nullToEmpty(v1);
        v2 = nullToEmpty(v2);
        separator = separator == null ? EMPTY_SEPARATOR : separator;

        if (v1.isEmpty() && v2.isEmpty()) {
            return null;
        }

        if (v1.isEmpty()) {
            return v2;
        }

        if (v2.isEmpty()) {
            return v1;
        }

        return v1 + separator + v2;
    }
}