package com.terry.backend.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsYNValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsYN {
    String message() default "{wadjet.validation.InvalidYNType}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
