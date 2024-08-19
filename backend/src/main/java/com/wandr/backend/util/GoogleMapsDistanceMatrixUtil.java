package com.wandr.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandr.backend.dto.trip.TripDurationAndDistanceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GoogleMapsDistanceMatrixUtil {

    @Value("${google.api.key}")
    private String apiKey;

    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private final RestTemplate restTemplate;

    public GoogleMapsDistanceMatrixUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TripDurationAndDistanceDTO calculateTripDurationAndDistance(String origin, String destination, List<String> intermediates) {
        int totalDistance = 0;
        int totalDuration = 0;

        // Calculate from origin to the first intermediate
        if (!intermediates.isEmpty()) {
            TripDurationAndDistanceDTO segmentResult = getSegmentDistanceAndDuration(origin, intermediates.get(0));
            totalDistance += segmentResult.getTotalDistance();
            totalDuration += segmentResult.getTotalDuration();

            // Calculate between intermediate points
            for (int i = 0; i < intermediates.size() - 1; i++) {
                segmentResult = getSegmentDistanceAndDuration(intermediates.get(i), intermediates.get(i + 1));
                totalDistance += segmentResult.getTotalDistance();
                totalDuration += segmentResult.getTotalDuration();
            }

            // Calculate from the last intermediate to the destination
            segmentResult = getSegmentDistanceAndDuration(intermediates.get(intermediates.size() - 1), destination);
            totalDistance += segmentResult.getTotalDistance();
            totalDuration += segmentResult.getTotalDuration();
        } else {
            // If no intermediates, just calculate origin to destination
            TripDurationAndDistanceDTO segmentResult = getSegmentDistanceAndDuration(origin, destination);
            totalDistance += segmentResult.getTotalDistance();
            totalDuration += segmentResult.getTotalDuration();
        }

        return new TripDurationAndDistanceDTO(totalDistance, totalDuration);
    }

    private TripDurationAndDistanceDTO getSegmentDistanceAndDuration(String origin, String destination) {
        // Build the request URL using lat/long coordinates for a single segment
        String url = DISTANCE_MATRIX_URL + "?origins=" + origin + "&destinations=" + destination + "&mode=driving&key=" + apiKey;

        // Set the necessary headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the GET request to the Google Maps API
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        // Extract duration and distance from the response
        return extractDurationAndDistanceFromResponse(response.getBody());
    }

    private TripDurationAndDistanceDTO extractDurationAndDistanceFromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            JsonNode elementNode = rootNode.path("rows").get(0).path("elements").get(0);

            int distance = elementNode.path("distance").path("value").asInt();
            int duration = elementNode.path("duration").path("value").asInt();

            return new TripDurationAndDistanceDTO(distance, duration);
        } catch (Exception e) {
            e.printStackTrace();
            return new TripDurationAndDistanceDTO(0, 0);
        }
    }
}
