package ru.tn.profitcalculator.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@ControllerAdvice
public class ApeExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleNotValidArgs(MethodArgumentNotValidException ex) {
        Map<String, String> errorsMap = ex.getBindingResult().getFieldErrors().stream()
                .collect(toMap(FieldError::getField, FieldError::getDefaultMessage));

        return new ResponseEntity<>(errorsMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, String>> handle500Error(Throwable th) {
        return new ResponseEntity<>(Collections.singletonMap("error", th.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
