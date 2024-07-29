package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.place.PlaceDTO;
import com.wandr.backend.dto.place.UpdatePlaceDTO;

import java.util.List;
import java.util.Map;

public interface PlaceService {

    PlaceDTO getPlaceById(long placeId);
    List<PlaceDTO> getAllPlaces();
    PlaceDTO update(long placeId, UpdatePlaceDTO updatePlaceDTO);
    ApiResponse<Void> delete(long placeId);

    ApiResponse<Void> fillDatabase(String location, int radius, int maxResults);
    void fetchAndSavePlaces(String url, int maxResults);
    ApiResponse<Void> getPlaceCategories(Long placeId);
    ApiResponse<Void> bulkCategorizePlaces();
    Map<String, Object> getPlaceDetailsFromAPI(String placeId);
    Map<String, Object> searchPlaceByNameFromAPI(String placeName);

    PlaceDTO add(String placeId);






    }
