package ru.inf_fans.web_hockey.controller.controllerAdvice;

public class NotFoundMatchException extends RuntimeException {
    public NotFoundMatchException(
            String message
    ) {
        super(message);
    }
}
