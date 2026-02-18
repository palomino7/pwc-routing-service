package com.daniel.routingservice.controller;

import com.daniel.routingservice.model.RouteResponse;
import com.daniel.routingservice.service.RoutingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routing")
public class RouteController {

    private final RoutingService routingService;

    public RouteController(RoutingService routingService) {

        this.routingService = routingService;
    }

    @GetMapping("/{origin}/{destination}")
    public ResponseEntity<RouteResponse> getRoute(
        @PathVariable String origin,
        @PathVariable String destination) {

        var route = routingService.findRoute(origin.toUpperCase(), destination.toUpperCase());

        return ResponseEntity.ok(route);
    }
}