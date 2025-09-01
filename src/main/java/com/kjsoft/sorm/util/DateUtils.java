package com.kjsoft.sorm.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Component
public class DateUtils {

    public OffsetDateTime now() {
        return OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}