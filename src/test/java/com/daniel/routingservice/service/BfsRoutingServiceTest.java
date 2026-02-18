package com.daniel.routingservice.service;

import com.daniel.routingservice.model.Country;
import com.daniel.routingservice.model.exception.CountryNotFoundException;
import com.daniel.routingservice.model.exception.RouteNotFoundException;
import com.daniel.routingservice.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BfsRoutingServiceTest {

    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private BfsRoutingService bfsRoutingService;

    private Map<String, Set<String>> bordersByCountry;

    @BeforeEach
    void setUp() {
        bordersByCountry = Map.of(
            "CZE", Set.of("AUT"),
            "AUT", Set.of("CZE", "ITA", "HUN"),
            "ITA", Set.of("AUT", "SVN"),
            "HUN", Set.of("AUT", "ROU", "SVN"),
            "ROU", Set.of("HUN"),
            "SVN", Set.of("ITA", "HUN"),
            "ISL", Set.of()
        );

        lenient().when(countryRepository.getBordersByCountry()).thenReturn(bordersByCountry);
    }

    @Test
    void findRoute_shouldReturnDirectRoute_whenCountriesShareBorder() {

        var origin = "CZE";
        var destination = "AUT";
        mockCountry(origin);
        mockCountry(destination);

        var result = bfsRoutingService.findRoute(origin, destination);

        assertThat(result).isNotNull();
        assertThat(result.route()).containsExactly(origin, destination);
    }

    @Test
    void findRoute_shouldReturnShortestRoute_whenMultipleHopsRequired() {

        var origin = "CZE";
        var destination = "ITA";
        mockCountry(origin);
        mockCountry(destination);

        var result = bfsRoutingService.findRoute(origin, destination);

        assertThat(result).isNotNull();
        assertThat(result.route()).containsExactly("CZE", "AUT", "ITA");
    }

    @Test
    void findRoute_shouldReturnLongerRoute_whenThreeHopsRequired() {

        var origin = "CZE";
        var destination = "ROU";
        mockCountry(origin);
        mockCountry(destination);

        var result = bfsRoutingService.findRoute(origin, destination);

        assertThat(result).isNotNull();
        assertThat(result.route()).containsExactly("CZE", "AUT", "HUN", "ROU");
        assertThat(result.route()).hasSize(4);
    }

    @Test
    void findRoute_shouldReturnShortestPath_whenMultiplePathsExist() {

        var origin = "CZE";
        var destination = "SVN";
        mockCountry(origin);
        mockCountry(destination);

        var result = bfsRoutingService.findRoute(origin, destination);

        assertThat(result).isNotNull();
        assertThat(result.route()).hasSize(4);
        assertThat(result.route().getFirst()).isEqualTo(origin);
        assertThat(result.route().getLast()).isEqualTo(destination);
        assertThat(result.route()).contains("AUT");
    }

    @Test
    void findRoute_shouldReturnSingleCountry_whenOriginEqualsDestination() {

        var origin = "CZE";
        var destination = "CZE";
        mockCountry(origin);

        var result = bfsRoutingService.findRoute(origin, destination);

        assertThat(result).isNotNull();
        assertThat(result.route()).containsExactly(origin);
    }

    @Test
    void findRoute_shouldThrowCountryNotFoundException_whenOriginDoesNotExist() {

        var origin = "XXX";
        var destination = "ITA";
        when(countryRepository.findByCca3(origin)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bfsRoutingService.findRoute(origin, destination))
            .isInstanceOf(CountryNotFoundException.class)
            .hasMessageContaining(origin);
    }

    @Test
    void findRoute_shouldThrowCountryNotFoundException_whenDestinationDoesNotExist() {

        var origin = "CZE";
        var destination = "ZZZ";
        mockCountry(origin);
        when(countryRepository.findByCca3(destination)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bfsRoutingService.findRoute(origin, destination))
            .isInstanceOf(CountryNotFoundException.class)
            .hasMessageContaining(destination);
    }

    @Test
    void findRoute_shouldThrowRouteNotFoundException_whenNoLandRouteExists() {

        var origin = "CZE";
        var destination = "ISL";
        mockCountry(origin);
        mockCountry(destination);

        assertThatThrownBy(() -> bfsRoutingService.findRoute(origin, destination))
            .isInstanceOf(RouteNotFoundException.class)
            .hasMessageContaining(origin)
            .hasMessageContaining(destination);
    }

    @Test
    void findRoute_shouldThrowRouteNotFoundException_whenStartingFromIsolatedCountry() {

        var origin = "ISL";
        var destination = "CZE";
        mockCountry(origin);
        mockCountry(destination);

        assertThatThrownBy(() -> bfsRoutingService.findRoute(origin, destination))
            .isInstanceOf(RouteNotFoundException.class)
            .hasMessageContaining(origin)
            .hasMessageContaining(destination);
    }

    private void mockCountry(String cca3) {

        var country = new Country(cca3, bordersByCountry.getOrDefault(cca3, Set.of()).stream().toList());
        when(countryRepository.findByCca3(cca3)).thenReturn(Optional.of(country));
    }
}