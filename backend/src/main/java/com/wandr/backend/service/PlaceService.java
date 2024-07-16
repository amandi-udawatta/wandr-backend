package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;

import java.util.Map;

public interface PlaceService {

    ApiResponse<Void> updatePlaces(String location, int radius, int maxResults);
    void fetchAndSavePlaces(String url, int maxResults);

    ApiResponse<Void> getPlaceCategories(Long placeId);

    ApiResponse<Void> getPlaceActivities(Long placeId);

    Map<String, Object> getPlaceDetails(String placeId);
    Map<String, Object> searchPlaceByName(String placeName);

    ApiResponse<Void> bulkCategorizePlaces();



    }
