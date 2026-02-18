package com.daniel.routingservice.service;

import com.daniel.routingservice.model.RouteResponse;

public interface RoutingService {

    RouteResponse findRoute(String origin, String destination);
}