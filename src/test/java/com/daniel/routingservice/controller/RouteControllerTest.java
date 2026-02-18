package com.daniel.routingservice.controller;

import com.daniel.routingservice.model.RouteResponse;
import com.daniel.routingservice.model.exception.CountryNotFoundException;
import com.daniel.routingservice.model.exception.RouteNotFoundException;
import com.daniel.routingservice.service.BfsRoutingService;
import com.daniel.routingservice.service.RoutingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

    private final RoutingService routingService = mock(BfsRoutingService.class);
    private final RouteController routeController = new RouteController(routingService);

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(routeController, new RouteExceptionHandler()).build();
    }

    @Test
    void findRoute_shouldReturnDirectRoute_whenRouteExists() throws Exception {

        when(routingService.findRoute("CZE", "ITA"))
            .thenReturn(new RouteResponse(List.of("CZE", "AUT", "ITA")));

        mockMvc.perform(get(RouteEndpoints.GET_ROUTE, "CZE", "ITA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.route").isArray())
            .andExpect(jsonPath("$.route[0]").value("CZE"))
            .andExpect(jsonPath("$.route[1]").value("AUT"))
            .andExpect(jsonPath("$.route[2]").value("ITA"))
            .andExpect(jsonPath("$.route.length()").value(3));
    }

    @Test
    void findRoute_shouldReturnDirectRoute_whenCountriesShareBorder() throws Exception {

        when(routingService.findRoute("CZE", "AUT"))
            .thenReturn(new RouteResponse(List.of("CZE", "AUT")));

        mockMvc.perform(get(RouteEndpoints.GET_ROUTE, "CZE", "AUT"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.route[0]").value("CZE"))
            .andExpect(jsonPath("$.route[1]").value("AUT"))
            .andExpect(jsonPath("$.route.length()").value(2));
    }

    @Test
    void findRoute_shouldReturnSingleCountry_whenOriginEqualsDestination() throws Exception {

        when(routingService.findRoute("CZE", "CZE"))
            .thenReturn(new RouteResponse(List.of("CZE")));

        mockMvc.perform(get(RouteEndpoints.GET_ROUTE, "CZE", "CZE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.route[0]").value("CZE"))
            .andExpect(jsonPath("$.route.length()").value(1));
    }

    @Test
    void findRoute_shouldReturnBadRequest_whenOriginCountryDoesNotExist() throws Exception {

        when(routingService.findRoute("XXX", "ITA"))
            .thenThrow(new CountryNotFoundException("XXX"));

        mockMvc.perform(get(RouteEndpoints.GET_ROUTE, "XXX", "ITA"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findRoute_shouldReturnBadRequest_whenDestinationCountryDoesNotExist() throws Exception {

        when(routingService.findRoute("CZE", "ZZZ"))
            .thenThrow(new CountryNotFoundException("ZZZ"));

        mockMvc.perform(get(RouteEndpoints.GET_ROUTE, "CZE", "ZZZ"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findRoute_shouldReturnBadRequest_whenNoLandRouteExists() throws Exception {

        when(routingService.findRoute("CZE", "AUS"))
            .thenThrow(new RouteNotFoundException("CZE", "AUS"));

        mockMvc.perform(get(RouteEndpoints.GET_ROUTE, "CZE", "AUS"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findRoute_shouldReturnNormalizedRoute_whenLowercaseCodesProvided() throws Exception {

        when(routingService.findRoute("CZE", "ITA"))
            .thenReturn(new RouteResponse(List.of("CZE", "AUT", "ITA")));

        mockMvc.perform(get(RouteEndpoints.GET_ROUTE, "cze", "ita"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.route[0]").value("CZE"));
    }

    @Test
    void findRoute_shouldReturnNormalizedRoute_whenMixedCaseCodesProvided() throws Exception {

        when(routingService.findRoute("CZE", "ITA"))
            .thenReturn(new RouteResponse(List.of("CZE", "AUT", "ITA")));

        mockMvc.perform(get(RouteEndpoints.GET_ROUTE, "CzE", "ItA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.route[0]").value("CZE"));
    }
}