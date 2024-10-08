package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.PlaceRatingDTO;
import com.wandr.backend.dto.trip.*;

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
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while creating trip"));
        }
    }

    @PostMapping("/add-place")
    public ResponseEntity<ApiResponse<Void>> addPlaceToTrip(@RequestBody AddPlaceToTripDTO addPlaceToTripDTO) {
        logger.info("Received request to add place to trip with placeId: {}", addPlaceToTripDTO.getPlaceId());
        try {
            ApiResponse<Void> response = tripService.addPlaceToTrip(addPlaceToTripDTO);
            logger.info("Successfully added place to trip with placeId: {}", addPlaceToTripDTO.getPlaceId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while adding place to trip with placeId: {}", addPlaceToTripDTO.getPlaceId(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while adding place to trip"));
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
    public ResponseEntity<ApiResponse<Void>> ratePlace(@RequestBody PlaceRatingDTO rating) {
        try {
            ApiResponse<Void> response = tripService.ratePlace(rating.getTravellerId(), rating.getPlaceId(), rating.getRating());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while rating place: " + e.getMessage()));
        }
    }

    @PostMapping("/shortest-route")
    public ApiResponse<Void> optimizeTrip(@RequestBody ShortestTripDTO shortestTripDTO) {
        logger.info("Received request to optimize trip with tripId: {}", shortestTripDTO.getTripId());
        try {
            ApiResponse<Void> response = tripService.optimizeTrip(shortestTripDTO.getTripId(), shortestTripDTO.getStartLat(), shortestTripDTO.getStartLng(), shortestTripDTO.getEndLat(), shortestTripDTO.getEndLng());
            logger.info("Successfully optimized trip with tripId: {}", shortestTripDTO.getTripId());
            return response;
        } catch (Exception e) {
            logger.error("An error occurred while optimizing trip with tripId: {}", shortestTripDTO.getTripId(), e);
            return new ApiResponse<>(false, 500, "An error occurred while optimizing trip");
        }
    }

    //change the current trip place orders with new orders given
    @PostMapping("/reorder-route")
    public ApiResponse<Void> reorderTrip(@RequestBody ReorderTripDTO reorderedTrip) {
        Long tripId = reorderedTrip.getTripId();
        logger.info("Received request to reorder trip with tripId: {}", tripId);
        try {
            ApiResponse<Void> response = tripService.reorderTrip(tripId, reorderedTrip.getPlaceList(), reorderedTrip.getStartLat(), reorderedTrip.getStartLng(), reorderedTrip.getEndLat(), reorderedTrip.getEndLng());
            logger.info("Successfully reordered trip with tripId: {}", tripId);
            return response;
        } catch (Exception e) {
            logger.error("An error occurred while reordering trip with tripId: {}", tripId, e);
            return new ApiResponse<>(false, 500, "An error occurred while reordering trip");
        }
    }

}
