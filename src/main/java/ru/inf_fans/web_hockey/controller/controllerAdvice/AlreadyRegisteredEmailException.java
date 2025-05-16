package ru.inf_fans.web_hockey.controller.controllerAdvice;

public class AlreadyRegisteredEmailException extends RuntimeException {

    public AlreadyRegisteredEmailException(
            String message
    ) {
        super(message);
    }
}
