package com.daniel.routingservice.model.exception;

public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException(String cca3) {

        super("Country not found: '%s'".formatted(cca3));
    }
}