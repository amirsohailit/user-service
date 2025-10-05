package com.mytestorg.user.infrastructure.adapter.input.rest.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/**
 * Custom validation annotation for passwords.
 * <p>
 * Ensures that the password is at least 8 characters long and includes at least one uppercase letter,
 * one lowercase letter, one number, and one special character.
 * </p>
 * <p>
 * Can be used on fields, methods, parameters, or other annotation types.
 */
@Documented
@Target({ElementType.FIELD})
@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).{8,}$")
@ReportAsSingleViolation

public @interface ValidPassword {
    String message() default "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
