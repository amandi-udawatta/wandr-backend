// GoogleMapsUtil.java
package com.wandr.backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class GoogleMapsUtil {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GoogleMapsUtil() {
        this.restTemplate = new RestTemplate();
    }

    public List<String> getShortestRoute(List<String> coordinates) {
        String url = buildDirectionsUrl(coordinates);

        // Make the API call
        DirectionsResponse response = restTemplate.getForObject(url, DirectionsResponse.class);
        System.out.println("response in getShortestRoute: " + response);

        // Process the response to get the ordered coordinates
        return processDirectionsResponse(coordinates, response);
    }

    private String buildDirectionsUrl(List<String> coordinates) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?origin=");
        url.append(coordinates.get(0));
        url.append("&destination=").append(coordinates.get(coordinates.size() - 1));
        url.append("&waypoints=optimize:true");

        for (int i = 1; i < coordinates.size() - 1; i++) {
            url.append("|").append(coordinates.get(i));
        }

        url.append("&key=").append(apiKey);
        return url.toString();
    }

    private List<String> processDirectionsResponse(List<String> originalCoordinates, DirectionsResponse response) {
        if (response != null && !response.getRoutes().isEmpty()) {
            List<Integer> waypointOrder = response.getRoutes().get(0).getWaypointOrder();

            // Original coordinates contain origin and destination as well, so we need to adjust the index.
            return IntStream.concat(IntStream.of(0), IntStream.concat(
                            waypointOrder.stream().mapToInt(i -> i + 1), IntStream.of(originalCoordinates.size() - 1)))
                    .mapToObj(originalCoordinates::get)
                    .collect(Collectors.toList());
        } else {
            // If no route found, return the original order
            return originalCoordinates;
        }
    }
}
