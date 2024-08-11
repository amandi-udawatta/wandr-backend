package com.wandr.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoogleMapsUtil {

    @Value("${google.api.key}")
    private String apiKey;

    private static final String ROUTE_OPTIMIZATION_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
    private final RestTemplate restTemplate;
    public GoogleMapsUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Integer> optimizeRoute(String origin,String destination, List<String> waypoints) {

        String jsonRequest = buildGoogleMapsRequest(origin,destination, waypoints);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("X-Goog-Api-Key", apiKey);
        headers.set("X-Goog-FieldMask", "routes.optimizedIntermediateWaypointIndex");

        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                ROUTE_OPTIMIZATION_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        return extractOptimizedOrderFromResponse(response.getBody());
    }

    private String buildGoogleMapsRequest(String origin,String destination, List<String> waypoints) {
        return "{ " +
                "\"origin\": { \"location\": { \"latLng\": { \"latitude\": " + origin.split(",")[0] + ", \"longitude\": " + origin.split(",")[1] + " } } }, " +
                "\"destination\": { \"location\": { \"latLng\": { \"latitude\": " + destination.split(",")[0] + ", \"longitude\": " + destination.split(",")[1] + " } } }, " +
                "\"intermediates\": " + waypoints.stream()
                .map(wp -> "{ \"location\": { \"latLng\": { \"latitude\": " + wp.split(",")[0] + ", \"longitude\": " + wp.split(",")[1] + " } } }")
                .collect(Collectors.joining(",", "[", "]")) + ", " +
                "\"travelMode\": \"DRIVE\", " +
                "\"optimizeWaypointOrder\": true " +
                "}";
    }

    private List<Integer> extractOptimizedOrderFromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode waypointIndexNode = rootNode
                    .path("routes")
                    .get(0)
                    .path("optimizedIntermediateWaypointIndex");

            List<Integer> optimizedOrder = new ArrayList<>();
            if (waypointIndexNode.isArray()) {
                for (JsonNode node : waypointIndexNode) {
                    optimizedOrder.add(node.asInt());
                }
            }

            return optimizedOrder;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
