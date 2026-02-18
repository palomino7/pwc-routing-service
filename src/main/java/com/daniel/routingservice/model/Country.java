package com.daniel.routingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Country(
    // Country Code Alpha-3
    String cca3,
    List<String> borders) {

    public Country {

        borders = borders != null ? List.copyOf(borders) : List.of();
    }
}