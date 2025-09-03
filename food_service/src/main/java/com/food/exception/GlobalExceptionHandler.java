package com.food.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity handlePasswordMismatchException(final PasswordMismatchException ex) {
        return  ResponseEntity. status(HttpStatus.UNAUTHORIZED).
                body(new ErrorResponse(ErrorCode.PASSWORD_MISMATCH_EXCEPTION, ex.getMessage()));
    }
    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity handleUserNotFoundException(final UserNotFoundException ex) {
        return  ResponseEntity. status(HttpStatus.NOT_FOUND).
                body(new ErrorResponse(ErrorCode.USER_NOT_FOUND, ex.getMessage()));
    }
}