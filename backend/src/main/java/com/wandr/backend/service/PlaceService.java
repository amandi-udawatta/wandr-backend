package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;

public interface PlaceService {

    ApiResponse<Void> updatePlaces(String location, int radius, int maxResults);
    void fetchAndSavePlaces(String url, int maxResults);





    }
