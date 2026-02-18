# Routing Service
REST API for calculating shortest land routes between countries.
- Java 21
- Spring Boot 3.5.10
- Maven 3.8+

# Build and Run
## Prerequisites
- Java 21+
- Maven 3.8+

## Build
```bash
  mvn clean package
```

## Run
```bash
  java -jar target/routing-service-1.0.0.jar
```
By default, the application is configured to run on port 8080: e.g.: `http://localhost:8080`

## API
```
GET /routing/{origin}/{destination}
```

**Parameters:** ISO 3166-1 alpha-3 country codes (e.g., CZE, ITA)

## Test - success response - 200
```bash
  curl -i http://localhost:8080/routing/CZE/ITA
```
## Test - bad request - 400
```bash
  curl -i -H "Accept: application/json" http://localhost:8080/routing/CZE/USA
```

## Configuration

`src/main/resources/application.yml`:
```yaml
spring:
  application:
    name: routing-service

server:
  port: 8080

app:
  countries:
    data-path: classpath:data/countries.json

logging:
  level:
    com.daniel.routingservice: INFO
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

## Development notes

BFS - guarantees shortest path (fewest borders) in O(V+E).

Data loaded at startup from JSON(countries.json), cached in-memory as adjacency graph.
