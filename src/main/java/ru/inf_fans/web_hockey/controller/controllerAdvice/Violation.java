package ru.inf_fans.web_hockey.controller.controllerAdvice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Violation {

    private final String fieldName;
    private final String message;

}
