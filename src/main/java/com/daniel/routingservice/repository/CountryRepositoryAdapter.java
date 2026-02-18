package com.daniel.routingservice.repository;

import com.daniel.routingservice.model.Country;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class CountryRepositoryAdapter implements CountryRepository {

    private static final Logger log = LoggerFactory.getLogger(CountryRepositoryAdapter.class);

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final String countriesDataPath;

    private Map<String, Set<String>> bordersByCountry;
    private Map<String, Country> countryByCca3;

    public CountryRepositoryAdapter(
        ObjectMapper objectMapper,
        ResourceLoader resourceLoader,
        @Value("${app.countries.data-path}") String countriesDataPath) {

        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.countriesDataPath = countriesDataPath;
    }

    @PostConstruct
    void initialize() throws IOException {

        log.info("Loading country data from: {}", countriesDataPath);

        var countries = loadCountriesFromResource(countriesDataPath);
        countryByCca3 = toCca3Map(countries, country -> country);
        bordersByCountry = toCca3Map(countries, country -> Set.copyOf(country.borders()));

        log.info("Loaded {} countries with border data into memory", countryByCca3.size());
    }

    private List<Country> loadCountriesFromResource(String path) throws IOException {

        var resource = resourceLoader.getResource(path);
        try (var inputStream = resource.getInputStream()) {

            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        }
    }

    private <T> Map<String, T> toCca3Map(List<Country> countries, Function<Country, T> valueMapper) {

        return countries.stream()
            .filter(country -> country.cca3() != null)
            .collect(Collectors.toUnmodifiableMap(
                Country::cca3,
                valueMapper));
    }

    @Override
    public Optional<Country> findByCca3(String cca3) {

        return Optional.ofNullable(countryByCca3.get(cca3));
    }

    @Override
    public Map<String, Set<String>> getBordersByCountry() {

        return bordersByCountry;
    }
}