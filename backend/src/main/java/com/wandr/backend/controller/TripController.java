package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.trip.AddPlaceToTripDTO;
import com.wandr.backend.dto.trip.CreateTripDTO;

import com.wandr.backend.dto.trip.PendingTripsDTO;
import com.wandr.backend.dto.RatingDTO;
import com.wandr.backend.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/trip")
public class TripController {

    private final TripService tripService;

    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createTrip(@RequestBody CreateTripDTO createTripDTO) {
        logger.info("Received request to create trip with name: {}", createTripDTO.getName());
        try {
            ApiResponse<Void> response = tripService.createTrip(createTripDTO);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while creating trip"));
        }
    }

    @PostMapping("/add-place")
    public ResponseEntity<ApiResponse<Void>> addPlaceToTrip(@RequestBody AddPlaceToTripDTO addPlaceToTripDTO) {
        logger.info("Received request to add place to trip with placeId: {}", addPlaceToTripDTO.getPlaceId());
        try{
            ApiResponse<Void> response = tripService.addPlaceToTrip(addPlaceToTripDTO);
            logger.info("Successfully added place to trip with placeId: {}", addPlaceToTripDTO.getPlaceId());
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            logger.error("An error occurred while adding place to trip with placeId: {}", addPlaceToTripDTO.getPlaceId(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while adding place to trip"));
        }

    }

    @PutMapping("/update-order/{tripId}")
    public ResponseEntity<ApiResponse<Void>> updateTripOrder(@PathVariable Long tripId, @RequestBody String orderType) {
        try {
            ApiResponse<Void> response = tripService.updateTripOrder(tripId, orderType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating trip order: " + e.getMessage()));
        }
    }

    //get pending trips of the traveller
    @GetMapping("/pending/{travellerId}")
    public ResponseEntity<ApiResponse<List<PendingTripsDTO>>> getPendingTrips(@PathVariable Long travellerId) {
        try {
            ApiResponse<List<PendingTripsDTO>> response = tripService.getPendingTrips(travellerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while getting pending trips: " + e.getMessage()));
        }
    }

    //get finalized trips of the traveller
    @GetMapping("/finalized/{travellerId}")
    public ResponseEntity<ApiResponse<List<PendingTripsDTO>>> getFinalizedTrips(@PathVariable Long travellerId) {
        try {
            ApiResponse<List<PendingTripsDTO>> response = tripService.getFinalizedTrips(travellerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while getting finalized trips: " + e.getMessage()));
        }
    }

    //get finalized trips of the traveller
    @GetMapping("/ongoing/{travellerId}")
    public ResponseEntity<ApiResponse<PendingTripsDTO>> getOngoingTrip(@PathVariable Long travellerId) {
        logger.info("Received request to get ongoing trips for travellerId: {}", travellerId);
        try {
            ApiResponse<PendingTripsDTO> response = tripService.getOngoingTrip(travellerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.info("An error occurred while getting ongoing trips: " + e.getMessage());
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while getting ongoing trips: " + e.getMessage()));
        }
    }

    //rating stars for trip place
    @PostMapping("/rate-place")
    public ResponseEntity<ApiResponse<Void>> ratePlace(@RequestBody RatingDTO rating) {
        try {
            ApiResponse<Void> response = tripService.ratePlace(rating.getId(), rating.getRating());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while rating place: " + e.getMessage()));
        }
    }
}
