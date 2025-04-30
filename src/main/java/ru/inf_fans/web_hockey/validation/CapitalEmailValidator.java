package ru.inf_fans.web_hockey.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class CapitalEmailValidator implements ConstraintValidator<EmailValid, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {
        if (value != null && !value.isEmpty()) {
            return !value.contains(" ") && (StringUtils.countOccurrencesOf(value, "@") == 1);
        }
        return true;
    }
}
