package ru.inf_fans.web_hockey.controller.controllerAdvice;

public class NotFoundUserException extends RuntimeException {

    public NotFoundUserException(
            String message
    ) {
        super(message);
    }
}
