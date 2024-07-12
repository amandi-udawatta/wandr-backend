//package com.wandr.backend.service.impl;
//
//import com.wandr.backend.dao.PlaceDAO;
//import com.wandr.backend.dto.ApiResponse;
//import com.wandr.backend.entity.Places; // Ensure the entity name matches
//import com.wandr.backend.entity.Places;
//import com.wandr.backend.service.PlaceService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class PlaceServiceImpl implements PlaceService {
//
//    private final PlaceDAO placeDAO;
//    private final RestTemplate restTemplate;
//
//    @Value("${google.api.key}")
//    private String apiKey;
//
//    @Value("${google.places.api.url}")
//    private String apiUrl;
//
//    public PlaceServiceImpl(PlaceDAO placeDAO, RestTemplate restTemplate) {
//        this.placeDAO = placeDAO;
//        this.restTemplate = restTemplate;
//    }
//
//    @Override
//    public ApiResponse<Void> updatePlaces(String location, int radius, int maxResults) {
//        String url = String.format("%s?location=%s&radius=%d&type=tourist_attraction&key=%s", apiUrl, location, radius, apiKey);
//        fetchAndSavePlaces(url, maxResults);
//        return new ApiResponse<>(true, 200, "Places updated successfully");
//    }
//
//    public void fetchAndSavePlaces(String url, int maxResults) {
//        int resultsCount = 0;
//        while (url != null && resultsCount < maxResults) {
//            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
//
//            if (response != null && response.containsKey("results")) {
//                List<Map<String, Object>> places = (List<Map<String, Object>>) response.get("results");
//
//                for (Map<String, Object> placeData : places) {
//                    if (resultsCount >= maxResults) break;
//                    Places place = new Places();
//                    place.setName((String) placeData.get("name"));
//                    place.setDescription((String) placeData.getOrDefault("editorial_summary", "No description available"));
//                    Map<String, Object> geometry = (Map<String, Object>) placeData.get("geometry");
//                    Map<String, Object> locationData = (Map<String, Object>) geometry.get("location");
//                    place.setLatitude((double) locationData.get("lat"));
//                    place.setLongitude((double) locationData.get("lng"));
//                    place.setAddress((String) placeData.getOrDefault("vicinity", "No address available"));
//                    placeDAO.save(place);
//                    resultsCount++;
//                }
//
//                if (response.containsKey("next_page_token")) {
//                    String nextPageToken = (String) response.get("next_page_token");
//                    url = apiUrl + "?pagetoken=" + nextPageToken + "&key=" + apiKey;
//                    try {
//                        Thread.sleep(2000); // wait for a few seconds before making the next request
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    url = null;
//                }
//            } else {
//                url = null;
//            }
//        }
//    }
//}
package com.wandr.backend.service.impl;

import com.wandr.backend.dao.PlaceDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.entity.Places;
import com.wandr.backend.service.PlaceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

@Service
public class PlaceServiceImpl implements PlaceService {

    private final PlaceDAO placeDAO;
    private final RestTemplate restTemplate;

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.places.api.url}")
    private String apiUrl;

    @Value("${wikipedia.api.url}")
    private String wikipediaApiUrl;

    @Value("${wikipedia.search.url}")
    private String wikipediaSearchUrl;

    public PlaceServiceImpl(PlaceDAO placeDAO, RestTemplate restTemplate) {
        this.placeDAO = placeDAO;
        this.restTemplate = restTemplate;
    }

    @Override
    public ApiResponse<Void> updatePlaces(String location, int radius, int maxResults) {
        String url = String.format("%s?location=%s&radius=%d&type=tourist_attraction&key=%s", apiUrl, location, radius, apiKey);
        fetchAndSavePlaces(url, maxResults);
        return new ApiResponse<>(true, 200, "Places updated successfully");
    }

    public void fetchAndSavePlaces(String url, int maxResults) {
        int resultsCount = 0;
        while (url != null && resultsCount < maxResults) {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> places = (List<Map<String, Object>>) response.get("results");

                for (Map<String, Object> placeData : places) {
                    List<String> types = (List<String>) placeData.get("types");
                    if (types == null || !types.contains("tourist_attraction") || types.contains("lodging")) {
                        continue;
                    }
                    if (resultsCount >= maxResults) break;
                    Places place = new Places();
                    place.setName((String) placeData.get("name"));
                    String vicinity = (String) placeData.getOrDefault("vicinity", "No description available");
                    place.setDescription(fetchPlaceDescription((String) placeData.get("name")));
                    Map<String, Object> geometry = (Map<String, Object>) placeData.get("geometry");
                    Map<String, Object> locationData = (Map<String, Object>) geometry.get("location");
                    place.setLatitude((double) locationData.get("lat"));
                    place.setLongitude((double) locationData.get("lng"));
                    String formattedAddress = (String) placeData.getOrDefault("formatted_address", vicinity);
                    place.setAddress(formattedAddress);
                    placeDAO.save(place);
                    resultsCount++;
                }

                if (response.containsKey("next_page_token")) {
                    String nextPageToken = (String) response.get("next_page_token");
                    url = apiUrl + "?pagetoken=" + nextPageToken + "&key=" + apiKey;
                    try {
                        Thread.sleep(2000); // wait for a few seconds before making the next request
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    url = null;
                }
            } else {
                url = null;
            }
        }
    }

    private String fetchPlaceDescription(String placeName) {
        String formattedTitle = placeName.replace(" ", "_");
        String wikipediaUrl = wikipediaApiUrl + formattedTitle;
        try {
            Map<String, Object> response = restTemplate.getForObject(wikipediaUrl, Map.class);
            if (response != null && response.containsKey("extract")) {
                return (String) response.get("extract");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                // Try a simplified title if the exact title is not found
                System.out.println("Page not found for title: " + formattedTitle);
                String simplifiedTitle = placeName.split("\\(")[0].trim().replace(" ", "_");
                wikipediaUrl = wikipediaApiUrl + simplifiedTitle;
                try {
                    Map<String, Object> response = restTemplate.getForObject(wikipediaUrl, Map.class);
                    if (response != null && response.containsKey("extract")) {
                        return (String) response.get("extract");
                    }
                } catch (HttpClientErrorException ex) {
                    if (ex.getStatusCode().value() == 404) {
                        // Perform a search as a last resort
                        System.out.println("Page not found for simplified title: " + simplifiedTitle);
                        String searchUrl = wikipediaSearchUrl + placeName.replace(" ", "%20");
                        try {
                            Map<String, Object> searchResponse = restTemplate.getForObject(searchUrl, Map.class);
                            if (searchResponse != null && searchResponse.containsKey("query")) {
                                Map<String, Object> query = (Map<String, Object>) searchResponse.get("query");
                                if (query.containsKey("search")) {
                                    List<Map<String, Object>> searchResults = (List<Map<String, Object>>) query.get("search");
                                    if (!searchResults.isEmpty()) {
                                        String searchTitle = (String) searchResults.get(0).get("title");
                                        wikipediaUrl = wikipediaApiUrl + searchTitle.replace(" ", "_");
                                        searchResponse = restTemplate.getForObject(wikipediaUrl, Map.class);
                                        if (searchResponse != null && searchResponse.containsKey("extract")) {
                                            return (String) searchResponse.get("extract");
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex1) {
                            System.out.println("Error performing search: " + ex1.getMessage());
                        }
                    }
                }
            }
        }
        return "No detailed description available.";
    }
}

