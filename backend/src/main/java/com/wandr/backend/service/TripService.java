package com.wandr.backend.service;

import com.wandr.backend.dto.trip.AddPlaceToTripDTO;
import com.wandr.backend.dto.trip.CreateTripDTO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.trip.PendingTripsDTO;
import com.wandr.backend.entity.Trip;
import com.wandr.backend.entity.TripPlace;

import java.util.List;

public interface TripService {
    ApiResponse<Void> createTrip(CreateTripDTO createTripDTO);
    ApiResponse<Void> addPlaceToTrip(AddPlaceToTripDTO addPlaceToTripDTO);

    ApiResponse<Void> updateTripOrder(Long tripId, String orderType);

    ApiResponse<List<PendingTripsDTO>> getPendingTrips(Long travellerId);

    ApiResponse<List<PendingTripsDTO>> getFinalizedTrips(Long travellerId);

    ApiResponse<PendingTripsDTO> getOngoingTrip(Long travellerId);

    ApiResponse<Void> ratePlace(Long tripPlaceId, Integer rating);


}
