package ru.inf_fans.web_hockey.controller.controllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMessage {
    private String message;
    private String stackTrace;
}
