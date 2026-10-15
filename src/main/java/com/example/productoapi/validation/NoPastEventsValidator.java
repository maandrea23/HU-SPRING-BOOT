package com.example.productoapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class NoPastEventsValidator implements ConstraintValidator<NoPastEvents, LocalDate> {
    @Override public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(LocalDate.now());
    }
}
