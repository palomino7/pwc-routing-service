package com.daniel.routingservice.service;

import com.daniel.routingservice.model.Country;
import com.daniel.routingservice.model.exception.CountryNotFoundException;
import com.daniel.routingservice.model.exception.RouteNotFoundException;
import com.daniel.routingservice.model.RouteResponse;
import com.daniel.routingservice.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BfsRoutingService implements RoutingService {

    private final CountryRepository countryRepository;

    public BfsRoutingService(CountryRepository countryRepository) {

        this.countryRepository = countryRepository;
    }

    @Override
    public RouteResponse findRoute(String origin, String destination) {

        var originCountry = getCountry(origin);
        var destinationCountry = getCountry(destination);

        var route = findShortestPath(originCountry.cca3(), destinationCountry.cca3());

        return new RouteResponse(route);
    }

    private Country getCountry(String code) {

        return countryRepository.findByCca3(code)
            .orElseThrow(() -> new CountryNotFoundException(code));
    }


    private List<String> findShortestPath(String origin, String destination) {

        if (origin.equals(destination)) {
            return List.of(origin);
        }

        var borderGraph = countryRepository.getBordersByCountry();
        var queue = new ArrayDeque<List<String>>();
        var visited = new HashSet<String>();

        queue.add(List.of(origin));
        visited.add(origin);

        while (!queue.isEmpty()) {
            var currentPath = queue.poll();
            var currentCountry = currentPath.getLast();

            for (var neighbour : borderGraph.getOrDefault(currentCountry, Set.of())) {
                if (visited.contains(neighbour)) {
                    continue;
                }

                var newPath = new ArrayList<>(currentPath);
                newPath.add(neighbour);

                if (neighbour.equals(destination)) {
                    return List.copyOf(newPath);
                }

                visited.add(neighbour);
                queue.add(newPath);
            }
        }

        throw new RouteNotFoundException(origin, destination);
    }
}