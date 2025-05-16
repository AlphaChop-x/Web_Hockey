package ru.inf_fans.web_hockey.controller.controllerAdvice;

public class AlreadyRegisteredPhoneNumberException extends RuntimeException {

    public AlreadyRegisteredPhoneNumberException(
            String message
    ) {
        super(message);
    }
}
