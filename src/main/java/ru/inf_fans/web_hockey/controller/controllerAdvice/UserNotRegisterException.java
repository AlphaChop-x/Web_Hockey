package ru.inf_fans.web_hockey.controller.controllerAdvice;

public class UserNotRegisterException extends RuntimeException {
    public UserNotRegisterException(
            String message
    ) {
        super(message);
    }
}
