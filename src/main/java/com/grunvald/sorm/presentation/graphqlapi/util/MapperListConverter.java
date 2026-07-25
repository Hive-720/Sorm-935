package com.grunvald.sorm.presentation.graphqlapi.util;

import org.mapstruct.Named;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public interface MapperListConverter {

    @Named("commaSeparatedToList")
    default List<String> commaSeparatedToList(String input) {
        if (!StringUtils.hasText(input)) {
            return List.of();
        }

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }
}