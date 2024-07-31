package com.wandr.backend.service;

import com.wandr.backend.dto.trip.AddPlaceToTripDTO;
import com.wandr.backend.dto.trip.CreateTripDTO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.entity.Trip;
import com.wandr.backend.entity.TripPlace;

public interface TripService {
    ApiResponse<Void> createTrip(CreateTripDTO createTripDTO);
    ApiResponse<Void> addPlaceToTrip(AddPlaceToTripDTO addPlaceToTripDTO);

    ApiResponse<Void> updateTripOrder(Long tripId, String orderType);
}
