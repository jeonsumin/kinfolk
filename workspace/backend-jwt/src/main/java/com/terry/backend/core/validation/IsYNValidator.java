package com.terry.backend.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsYNValidator implements ConstraintValidator<IsYN, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || "Y".equalsIgnoreCase(value) || "N".equalsIgnoreCase(value);
    }
}
