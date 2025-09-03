package com.bank.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MinimumBalanceException.class)
    public ResponseEntity<ErrorResponse> handleMinimumBalanceException(MinimumBalanceException ex) {
        return  ResponseEntity. status(HttpStatus.BAD_REQUEST).
                body(new ErrorResponse(ErrorCode.MINIMUM_BALANCE, ex.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
        return  ResponseEntity. status(HttpStatus.NOT_FOUND).
                body(new ErrorResponse(ErrorCode.BANK_ACCOUNT_NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceExceptionException(InsufficientBalanceException ex) {
        return  ResponseEntity. status(HttpStatus.BAD_REQUEST).
                body(new ErrorResponse(ErrorCode.INSUFFICIENT_BALANCE, ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getRootCause().getMessage();

        if (message.contains("phone_number_unique_constraint")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS,"Phone number already exists"));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorCode.DB_CONSTRAINT_VOILATED,"Database constraint violated"));
    }

}