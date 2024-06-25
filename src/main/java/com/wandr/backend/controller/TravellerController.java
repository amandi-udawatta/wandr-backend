package com.wandr.backend.controller;

import com.wandr.backend.dto.*;
import com.wandr.backend.service.TravellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/travellers")
public class TravellerController {

    private final TravellerService travellerService;
    private static final Logger logger = LoggerFactory.getLogger(TravellerController.class);

    @Autowired
    public TravellerController(TravellerService travellerService) {
        this.travellerService = travellerService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody TravellerLoginDTO request) {
        logger.info("Received request to login traveller with email: {}", request.getEmail());
        try {
            ApiResponse<String> response = travellerService.loginTraveller(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while log in traveller with email: {}", request.getEmail(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while log in traveller"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody TravellerSignupDTO request) {
        logger.info("Received request to register traveller with email: {}", request.getEmail());
        try {
            ApiResponse<String> response = travellerService.registerTraveller(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while registering traveller with email: {}", request.getEmail(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while registering traveller"));
        }
    }

    @PutMapping("/{travellerId}/categories")
    public ResponseEntity<ApiResponse<String>> updateCategories(@PathVariable Long travellerId, @RequestBody UpdateCategoriesDTO request) {
        logger.info("Received request to update categories for traveller with ID: {}", travellerId);
        try {
            ApiResponse<String> response = travellerService.updateCategories(travellerId, request);
            logger.info("Categories updated successfully for traveller with ID: {}", travellerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while updating categories for traveller with ID: {}", travellerId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating categories"));
        }
    }

    @PutMapping("/{travellerId}/activities")
    public ResponseEntity<ApiResponse<String>> updateActivities(@PathVariable Long travellerId, @RequestBody UpdateActivitiesDTO request) {
        logger.info("Received request to update activities for traveller with ID: {}", travellerId);
        try {
            ApiResponse<String> response = travellerService.updateActivities(travellerId, request);
            logger.info("Activities updated successfully for traveller with ID: {}", travellerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while updating activities for traveller with ID: {}", travellerId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating activities"));
        }
    }

    @PutMapping("/{travellerId}/profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(@PathVariable Long travellerId, @RequestBody UpdateProfileDTO request) {
        logger.info("Received request to update profile for traveller with ID: {}", travellerId);
        try {
            ApiResponse<String> response = travellerService.updateProfile(travellerId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while updating profile for traveller with ID: {}", travellerId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating profile"));
        }
    }
}
