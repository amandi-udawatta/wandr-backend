package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.recommendation.RecommendedPlaceDTO;
import com.wandr.backend.dto.trip.AddPlaceToTripDTO;
import com.wandr.backend.dto.trip.CreateTripDTO;
import com.wandr.backend.dto.trip.PendingTripsDTO;

import java.util.List;

public interface RecommendationService {
    List<RecommendedPlaceDTO> getRecommendedPlaces(Long travellerId);



}
