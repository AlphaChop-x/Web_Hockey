package ru.inf_fans.web_hockey.controller.controllerAdvice;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        final List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> onDataIntegrityViolationException(
            DataIntegrityViolationException exception) {

        String message = String.format("%s %s", exception.getMostSpecificCause().getMessage(), LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("", message));
    }

    @ExceptionHandler(NotFoundTournamentException.class)
    public ResponseEntity<ErrorMessage> onNotFoundTournamentException(
            NotFoundTournamentException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Ошибка поиска турнира", exception.getMessage()));
    }
    @ExceptionHandler(NotFoundMatchException.class)
    public ResponseEntity<ErrorMessage> onNotFoundMatchException(
            NotFoundMatchException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Ошибка поиска матча", exception.getMessage()));
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<ErrorMessage> onNotFoundUserException(
            NotFoundUserException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Ошибка поиска игрока", exception.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessage> onAuthenticationException(
            AuthenticationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage("Ошибка авторизации", exception.getMessage()));
    }

    @ExceptionHandler(UserNotRegisterException.class)
    public ResponseEntity<ErrorMessage> onUserNotRegisterException(
            UserNotRegisterException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Ошибка регистрации пользователя", exception.getMessage()));
    }

    @ExceptionHandler(AlreadyRegisteredEmailException.class)
    public ResponseEntity<ErrorMessage> onAlreadyRegisteredEmailException(
            AlreadyRegisteredEmailException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("Пользователь с такой почтой уже зарегистрирован", exception.getMessage()));
    }

    @ExceptionHandler(AlreadyRegisteredPhoneNumberException.class)
    public ResponseEntity<ErrorMessage> onAlreadyRegisteredPhoneNumberException(
            AlreadyRegisteredPhoneNumberException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("Пользователь с таким номером телефона уже зарегистрирован", exception.getMessage()));
    }


    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(ChangeSetPersister.NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> mismatchException(MethodArgumentTypeMismatchException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("", exception.getMessage()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorMessage> duplicateKeyException(DuplicateKeyException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("Ошибка дубликата ключей ", exception.getMessage()));
    }

}
