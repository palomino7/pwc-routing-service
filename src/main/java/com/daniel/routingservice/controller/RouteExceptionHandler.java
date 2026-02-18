package com.daniel.routingservice.controller;

import com.daniel.routingservice.model.exception.CountryNotFoundException;
import com.daniel.routingservice.model.exception.RouteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RouteExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RouteExceptionHandler.class);

    @ExceptionHandler({
        CountryNotFoundException.class,
        RouteNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestExceptions(RuntimeException runtimeException) {

        log.error("Bad request exception occurred:", runtimeException);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {

        log.error("Exception occurred:", exception);

        return ResponseEntity.internalServerError().build();
    }
}