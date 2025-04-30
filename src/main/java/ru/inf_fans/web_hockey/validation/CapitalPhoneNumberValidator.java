package ru.inf_fans.web_hockey.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CapitalPhoneNumberValidator implements ConstraintValidator<PhoneNumberValid, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        if (value != null && !value.isEmpty()) {
            return (value.startsWith("+79")) && (!value.contains(" ") && value.length() == 12);
        }
        return true;
    }
}
