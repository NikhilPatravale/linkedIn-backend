package com.linkedIn.linkedIn.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class BackEndController {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String , String>> httpMessageNoReadableExceptionHandler(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(Map.of("message", "Request body not present"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(Map.of("message", "Mandatory field/s is/are incorrect/missing in request body"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> dataIntegrityErrorHandler(DataIntegrityViolationException e) {
        if (e.getMessage().contains("Duplicate entry")) {
            return new ResponseEntity<>(Map.of("message", "Email address already registered, please try with new email"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> illegalMethodArgumentExceptionHandler(IllegalArgumentException e) {
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<Map<String, String>> mailSendExceptionHandler(MailSendException e) {
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> generalErrorHandler(Exception e) {
        e.getStackTrace();
        return new ResponseEntity<>(Map.of("message", "Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
