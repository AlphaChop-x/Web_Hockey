package ru.inf_fans.web_hockey.controller.controllerAdvice;

public class NotFoundTournamentException extends RuntimeException {

    public NotFoundTournamentException(
            String message
    ) {
        super(message);
    }
}