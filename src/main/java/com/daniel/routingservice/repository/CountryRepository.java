package com.daniel.routingservice.repository;

import com.daniel.routingservice.model.Country;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CountryRepository {

    Optional<Country> findByCca3(String cca3);

    Map<String, Set<String>> getBordersByCountry();
}