package com.grunvald.sorm.presentation.api.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {
    private List<String> acceptedValues;
    boolean ignoreCase;


    @Override
    public void initialize(ValueOfEnum annotation) {
        ignoreCase = annotation.ignoreCase();
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (ignoreCase) {
            value = value.toUpperCase();
        }

        return acceptedValues.contains(value);
    }
}