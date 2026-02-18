package com.daniel.routingservice.controller;

import com.daniel.routingservice.IntegrationTest;
import com.daniel.routingservice.model.RouteResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static com.daniel.routingservice.controller.RouteEndpoints.GET_ROUTE;
import static org.assertj.core.api.Assertions.assertThat;

class RouteControllerIntegrationTest extends IntegrationTest {

    @Test
    void findRoute_shouldReturnCompleteRoute_whenCountriesAreValid() throws Exception {

        var origin = "CZE";
        var destination = "ITA";

        var result = performRequestAndReturnResponse(
            GET_ROUTE,
            RouteResponse.class,
            origin,
            destination);

        assertThat(result).isNotNull();
        assertThat(result.route()).isNotEmpty();
        assertThat(result.route().getFirst()).isEqualTo(origin);
        assertThat(result.route().getLast()).isEqualTo(destination);
        assertThat(result.route()).contains("AUT");
        assertThat(result.route()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findRoute_shouldReturnDirectRoute_whenCountriesShareBorder() throws Exception {

        var origin = "CZE";
        var destination = "AUT";

        var result = performRequestAndReturnResponse(
            GET_ROUTE,
            RouteResponse.class,
            origin,
            destination);

        assertThat(result).isNotNull();
        assertThat(result.route()).containsExactly(origin, destination);
        assertThat(result.route()).hasSize(2);
    }

    @Test
    void findRoute_shouldReturnSingleCountry_whenOriginEqualsDestination() throws Exception {

        var origin = "CZE";
        var destination = "CZE";

        var result = performRequestAndReturnResponse(
            GET_ROUTE,
            RouteResponse.class,
            origin,
            destination
        );

        assertThat(result).isNotNull();
        assertThat(result.route()).containsExactly(origin);
        assertThat(result.route()).hasSize(1);
    }

    @Test
    void findRoute_shouldHandleLowercaseInput_whenCountryCodesInLowercase() throws Exception {

        var origin = "cze";
        var destination = "ita";

        var result = performRequestAndReturnResponse(
            GET_ROUTE,
            RouteResponse.class,
            origin,
            destination);

        assertThat(result).isNotNull();
        assertThat(result.route()).isNotEmpty();
        assertThat(result.route().getFirst()).isEqualTo("CZE");
        assertThat(result.route().getLast()).isEqualTo("ITA");
    }

    static Stream<Arguments> invalidCountryScenarios() {

        return Stream.of(
            Arguments.of("XXX", "ITA", "Origin country does not exist"),
            Arguments.of("CZE", "ZZZ", "Destination country does not exist"),
            Arguments.of("CZE", "AUS", "No land route exists")
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("invalidCountryScenarios")
    void findRoute_shouldReturnBadRequest_forInvalidCountries(
        String origin,
        String destination,
        String reason)
        throws Exception {

        var result = performRequestAndReturnResponse(GET_ROUTE, origin, destination);

        assertThat(result.getResponse().getStatus())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}