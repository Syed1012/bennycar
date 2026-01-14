package de.bennycar.worldview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import de.bennycar.worldview.infrastructure.adapter.inbound.web.dto.RouteDto;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Application tests for World View Service.
 * Tests basic route endpoints (journeys are auto-managed).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class WorldViewApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Context loads successfully
    }

    @Test
    void getAllRoutes_shouldReturn8Routes() {
        ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/routes",
            RouteDto[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        RouteDto[] body1 = Objects.requireNonNull(
                response.getBody(),
                "Response body cannot be null"
        );
        assertEquals(8, body1.length);
    }

    @Test
    void getRouteById_shouldReturnRoute() {
        ResponseEntity<RouteDto> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/routes/route-1",
            RouteDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        RouteDto body2 = Objects.requireNonNull(
                response.getBody(),
                "Response body cannot be null"
        );
        assertEquals("route-1", body2.getId());
        assertEquals("Ludwigsburg Schloss Route", body2.getName());
    }

    @Test
    void getRouteCount_shouldReturn8() {
        ResponseEntity<Integer> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/routes/count",
            Integer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Integer body3 = Objects.requireNonNull(
                response.getBody(),
                "Response body cannot be null"
        );
        assertEquals(8, body3);
    }

    @Test
    void allRoutes_shouldEndAtDealership() {
        ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/routes",
            RouteDto[].class
        );

        RouteDto[] body4 = Objects.requireNonNull(
                response.getBody(),
                "Response body cannot be null"
        );
        for (RouteDto route : body4) {
            // All routes should end at Dealership
            assertEquals(48.8354, route.getEndPoint().getLatitude(), 0.0001,
                "Route " + route.getId() + " should end at dealership latitude");
            assertEquals(9.152, route.getEndPoint().getLongitude(), 0.0001,
                "Route " + route.getId() + " should end at dealership longitude");
        }
    }

    @Test
    void journeyActiveEndpoint_shouldBeAccessible() {
        ResponseEntity<Boolean> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/journeys/active",
            Boolean.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Objects.requireNonNull(
                response.getBody(),
                "Response body cannot be null"
        );
    }
}

