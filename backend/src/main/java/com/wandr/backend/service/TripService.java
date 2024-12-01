package com.wandr.backend.service;

import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.dto.trip.*;
import com.wandr.backend.dto.ApiResponse;

import java.util.List;

public interface TripService {
    ApiResponse<Void> createTrip(CreateTripDTO createTripDTO);
    ApiResponse<Void> addPlaceToTrip(AddPlaceToTripDTO addPlaceToTripDTO);

    ApiResponse<List<PendingTripsDTO>> getPendingTrips(Long travellerId);

    ApiResponse<List<PendingTripsDTO>> getFinalizedTrips(Long travellerId);

    ApiResponse<PendingTripsDTO> getOngoingTrip(Long travellerId);

    ApiResponse<Void> ratePlace(Long travellerId, Long placeId, Integer rating);

    ApiResponse<TripTimeDTO> reorderTrip(Long tripId, List<PlaceOrderDTO> placeOrderList, double startLat, double startLng, double endLat, double endLng );

    ApiResponse<TripTimeDTO> optimizeTrip(Long tripId, double startLat, double startLng, double endLat, double endLng);

    ApiResponse<List<DashboardPlaceDTO>> getRecommendedPlacesForTrip(Long tripId);



    }
