package de.bennycar.worldview.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import de.bennycar.worldview.infrastructure.adapter.inbound.web.dto.JourneyStateDto;
import de.bennycar.worldview.infrastructure.adapter.inbound.web.dto.RouteDto;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the World View Service.
 * Tests the complete flow from REST API to database and back.
 *
 * Note: Journeys are now auto-managed, so manual journey control endpoints are removed.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("World View Integration Tests")
class WorldViewIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1";
    }

    @Nested
    @DisplayName("Route API Integration Tests")
    class RouteApiTests {

        @Test
        @DisplayName("Should return all 8 predefined routes")
        void shouldReturnAll8Routes() {
            // When
            ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
                    baseUrl + "/routes",
                    RouteDto[].class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            RouteDto[] body = Objects.requireNonNull(
                    response.getBody(),
                    "Response body cannot be null"
            );
            assertEquals(8, body.length);

            // Verify all routes have valid structure
            for (RouteDto route : body) {
                assertNotNull(route.getId());
                assertNotNull(route.getName());
                assertNotNull(route.getStartPoint());
                assertNotNull(route.getEndPoint());
                assertNotNull(route.getWaypoints());
                assertTrue(route.getWaypoints().size() >= 2);
                assertTrue(route.getTotalDistanceMeters() > 0);
                assertTrue(route.getEstimatedDurationSeconds() > 0);
            }
        }

        @Test
        @DisplayName("All routes should end at Dealership")
        void allRoutesShouldEndAtDealership() {
            // Given
            double dealershipLat = 48.8354;
            double dealershipLng = 9.1520;

            // When
            ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
                    baseUrl + "/routes",
                    RouteDto[].class
            );

            // Then
            RouteDto[] body1 = Objects.requireNonNull(
                    response.getBody(),
                    "Response body cannot be null"
            );
            for (RouteDto route : body1) {
                assertEquals(dealershipLat, route.getEndPoint().getLatitude(), 0.0001,
                        "Route " + route.getId() + " should end at dealership latitude");
                assertEquals(dealershipLng, route.getEndPoint().getLongitude(), 0.0001,
                        "Route " + route.getId() + " should end at dealership longitude");
            }
        }

        @Test
        @DisplayName("Routes should start from different Stuttgart area locations")
        void routesShouldStartFromDifferentLocations() {
            // When
            ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
                    baseUrl + "/routes",
                    RouteDto[].class
            );

            // Then
            assertNotNull(response.getBody());

            // Verify routes have different starting points
            RouteDto[] body10 = Objects.requireNonNull(
                    response.getBody(),
                    "Response body cannot be null"
            );
            long uniqueStartPoints = java.util.Arrays.stream(body10)
                    .map(r -> r.getStartPoint().getLatitude() + "," + r.getStartPoint().getLongitude())
                    .distinct()
                    .count();

            assertEquals(8, uniqueStartPoints, "All 8 routes should have unique starting points");
        }

        @Test
        @DisplayName("Should get specific route by ID")
        void shouldGetRouteById() {
            // When
            ResponseEntity<RouteDto> response = restTemplate.getForEntity(
                    baseUrl + "/routes/route-1",
                    RouteDto.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            RouteDto body3 = Objects.requireNonNull(
                    response.getBody(),
                    "Response body cannot be null"
            );
            assertEquals("route-1", body3.getId());
            assertEquals("Ludwigsburg Schloss Route", body3.getName());
        }

        @Test
        @DisplayName("Should return 404 for non-existent route")
        void shouldReturn404ForNonExistentRoute() {
            // When
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/routes/non-existent",
                    String.class
            );

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Should get route count")
        void shouldGetRouteCount() {
            // When
            ResponseEntity<Integer> response = restTemplate.getForEntity(
                    baseUrl + "/routes/count",
                    Integer.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Integer body8 = Objects.requireNonNull(
                    response.getBody(),
                    "Response body cannot be null"
            );
            assertEquals(8, body8);
        }
    }

    @Nested
    @DisplayName("Journey API Integration Tests (Auto-Managed)")
    class JourneyApiTests {

        @Test
        @DisplayName("Should check if journey is active")
        void shouldCheckIfJourneyIsActive() {
            // When
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    baseUrl + "/journeys/active",
                    Boolean.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Boolean body9 = Objects.requireNonNull(
                    response.getBody(),
                    "Response body cannot be null"
            );
            // Initially might be true or false depending on scheduler timing
        }

        @Test
        @DisplayName("Should get current journey if active")
        void shouldGetCurrentJourneyIfActive() {
            // When
            ResponseEntity<JourneyStateDto> response = restTemplate.getForEntity(
                    baseUrl + "/journeys/current",
                    JourneyStateDto.class
            );

            // Then - Either 200 with journey or 204 no content
            assertTrue(response.getStatusCode() == HttpStatus.OK ||
                       response.getStatusCode() == HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("Should return 404 for non-existent journey")
        void shouldReturn404ForNonExistentJourney() {
            // When
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/journeys/non-existent-journey",
                    String.class
            );

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Route Data Validation Tests")
    class RouteDataValidationTests {

        @Test
        @DisplayName("Route 1 should be from Ludwigsburg Schloss")
        void route1ShouldBeFromLudwigsburg() {
            ResponseEntity<RouteDto> response = restTemplate.getForEntity(
                    baseUrl + "/routes/route-1",
                    RouteDto.class
            );

            RouteDto body4 = Objects.requireNonNull(
                    response.getBody(),
                    "Response body cannot be null"
            );
            assertEquals("Ludwigsburg Schloss Route", body4.getName());
            // Ludwigsburg Schloss coordinates
            assertEquals(48.8973, body4.getStartPoint().getLatitude(), 0.001);
            assertEquals(9.1920, body4.getStartPoint().getLongitude(), 0.001);
        }

        @Test
        @DisplayName("Route 2 should be from Favoritepark")
        void route2ShouldBeFromFavoritepark() {
            ResponseEntity<RouteDto> response = restTemplate.getForEntity(
                    baseUrl + "/routes/route-2",
                    RouteDto.class
            );

            RouteDto body5 = Objects.requireNonNull(
                    response.getBody(),
                    "Response body cannot be null"
            );
            assertEquals("Favoritepark Route", body5.getName());
        }

        @Test
        @DisplayName("Route waypoints should form a continuous path")
        void routeWaypointsShouldFormContinuousPath() {
            ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
                    baseUrl + "/routes",
                    RouteDto[].class
            );

            RouteDto[] body6 = Objects.requireNonNull(
                    response.getBody(),
                    "Response body cannot be null"
            );
            for (RouteDto route : body6) {
                var waypoints = route.getWaypoints();

                // First waypoint should match start point
                assertEquals(route.getStartPoint().getLatitude(),
                        waypoints.get(0).getLatitude(), 0.0001);
                assertEquals(route.getStartPoint().getLongitude(),
                        waypoints.get(0).getLongitude(), 0.0001);

                // Last waypoint should match end point
                var lastWaypoint = waypoints.get(waypoints.size() - 1);
                assertEquals(route.getEndPoint().getLatitude(),
                        lastWaypoint.getLatitude(), 0.0001);
                assertEquals(route.getEndPoint().getLongitude(),
                        lastWaypoint.getLongitude(), 0.0001);
            }
        }
    }
}

