package com.daniel.routingservice.model;

import java.util.List;

public record RouteResponse(List<String> route) {

    public RouteResponse {

        route = List.copyOf(route);
    }
}