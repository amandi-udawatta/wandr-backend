package com.wandr.backend.service;

import com.wandr.backend.dto.trip.AddPlaceToTripDTO;
import com.wandr.backend.dto.trip.CreateTripDTO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.trip.PendingTripsDTO;
import com.wandr.backend.dto.trip.PlaceOrderDTO;

import java.util.List;

public interface TripService {
    ApiResponse<Void> createTrip(CreateTripDTO createTripDTO);
    ApiResponse<Void> addPlaceToTrip(AddPlaceToTripDTO addPlaceToTripDTO);

    ApiResponse<List<PendingTripsDTO>> getPendingTrips(Long travellerId);

    ApiResponse<List<PendingTripsDTO>> getFinalizedTrips(Long travellerId);

    ApiResponse<PendingTripsDTO> getOngoingTrip(Long travellerId);

    ApiResponse<Void> ratePlace(Long tripPlaceId, Integer rating);

    ApiResponse<Void> reorderTrip(Long tripId, List<PlaceOrderDTO> placeOrderList, double startLat, double startLng, double endLat, double endLng);

    ApiResponse<Void> optimizeTrip(Long tripId, double startLat, double startLng, double endLat, double endLng);


}
