package com.grunvald.sorm.presentation.graphqlapi.util;

import com.grunvald.sorm.presentation.graphqlapi.error.BadRequestException;
import org.apache.logging.log4j.util.Strings;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;


@Component
public class MapperOffsetDateTimeConverter {

    @Named("stringToUtcOffsetDateTime")
    public OffsetDateTime stringToUtcOffsetDateTime(String input) {
        if (!Strings.isNotBlank(input)) {
            return null;
        }

        try {
            return OffsetDateTime.parse(input, ISO_OFFSET_DATE_TIME)
                    .withOffsetSameInstant(ZoneOffset.UTC);
        } catch (Exception e) {
            throw new BadRequestException("Couldn't parse the date: " + input);
        }
    }

    @Named("offsetDateTimeToUtcString")
    public String offsetDateTimeToUtcString(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        var time = offsetDateTime.atZoneSameInstant(ZoneOffset.UTC);
        return time.format(ISO_OFFSET_DATE_TIME).replace("Z", "+00:00"); //TODO: возможно replace не нужен
    }

    @Named("stringListToUtcOffsetDateTimeList")
    public List<OffsetDateTime> stringListToUtcOffsetDateTimeList(List<String> input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        return input.stream()
                .filter(Strings::isNotBlank)
                .map(this::stringToUtcOffsetDateTime)
                .collect(Collectors.toList());
    }
}