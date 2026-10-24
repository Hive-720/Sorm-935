package com.grunvald.sorm.presentation.api.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValueOfEnumValidatorTest {

    enum TestEnum { FIRST, SECOND }

    static class Payload {
        @ValueOfEnum(enumClass = TestEnum.class)
        String strict;

        @ValueOfEnum(enumClass = TestEnum.class, ignoreCase = true)
        String lenient;

        Payload(String strict, String lenient) {
            this.strict = strict;
            this.lenient = lenient;
        }
    }

    static ValidatorFactory factory;
    static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (factory != null) factory.close();
    }

    private static boolean hasViolation(Set<ConstraintViolation<Payload>> violations, String field) {
        return violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(field));
    }

    @Test
    void strict_validValue_passes() {
        var violations = validator.validate(new Payload("FIRST", null));
        assertFalse(hasViolation(violations, "strict"));
    }

    @Test
    void strict_invalidValue_fails() {
        var violations = validator.validate(new Payload("NOPE", null));
        assertTrue(hasViolation(violations, "strict"));
    }

    @Test
    void strict_lowercase_failsWhenCaseSensitive() {
        var violations = validator.validate(new Payload("first", null));
        assertTrue(hasViolation(violations, "strict"));
    }

    @Test
    void lenient_lowercase_passesWhenIgnoreCase() {
        var violations = validator.validate(new Payload(null, "first"));
        assertFalse(hasViolation(violations, "lenient"));
    }

    @Test
    void lenient_invalidValue_fails() {
        var violations = validator.validate(new Payload(null, "third"));
        assertTrue(hasViolation(violations, "lenient"));
    }

    @Test
    void nullValue_passesBecauseOptional() {
        var violations = validator.validate(new Payload(null, null));
        assertTrue(violations.isEmpty());
    }

    @Test
    void isValid_null_returnsTrueDirectly() {
        var validatorImpl = new ValueOfEnumValidator();

        assertTrue(validatorImpl.isValid(null, null));
    }
}
