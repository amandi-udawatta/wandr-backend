package com.wandr.backend.controller;

import com.wandr.backend.dto.*;
import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.dto.place.PlaceDTO;
import com.wandr.backend.dto.place.UpdatePlaceDTO;
import com.wandr.backend.dto.traveller.*;
import com.wandr.backend.service.TravellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/traveller")
public class TravellerController {

    private final TravellerService travellerService;
    private static final Logger logger = LoggerFactory.getLogger(TravellerController.class);

    @Autowired
    public TravellerController(TravellerService travellerService) {
        this.travellerService = travellerService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDetailsDTO>> login(@RequestBody UserLoginDTO request) {
        logger.info("Received request to login traveller with email: {}", request.getEmail());
        try {
            ApiResponse<UserDetailsDTO> response = travellerService.loginTraveller(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while logging in traveller with email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, 500, "An error occurred while logging in traveller"));
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDetailsDTO>> signup(@RequestBody TravellerSignupDTO request) {
        logger.info("Received request to register traveller with email: {}", request.getEmail());
        try {
            ApiResponse<UserDetailsDTO> response = travellerService.registerTraveller(request);
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

    @PostMapping("/update/{travellerId}")
    public ResponseEntity<ApiResponse<TravellerDTO>> updateProfile(@PathVariable Long travellerId, @RequestBody UpdateProfileDTO request) {
        logger.info("Received request to update profile for traveller with ID: {}", travellerId);
        try {
            ApiResponse<TravellerDTO> response = travellerService.updateProfile(travellerId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while updating profile for traveller with ID: {}", travellerId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating profile"));
        }
    }

    @PostMapping("/save-jwt")
    public ResponseEntity<ApiResponse<Void>> saveJwtToken(@RequestBody Map<String, String> requestMap) {
        long travellerId = Long.parseLong(requestMap.get("userId"));
        String jwtToken = requestMap.get("jwtToken");
        logger.info("Received request to save JWT token for traveller with ID: {}", travellerId);

        try {
            travellerService.updateTravellerJwt(jwtToken, travellerId);
            logger.info("Successfully saved JWT token for traveller with ID: {}", travellerId);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "JWT token saved", null));
        }
        catch (Exception e) {
            logger.error("Error saving JWT token for traveller with ID {}: {}", travellerId, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Failed to save JWT token", null));
        }
    }

    @GetMapping("/get-salt")
    public ResponseEntity<ApiResponse<String>> getSalt(@RequestParam Map<String, String> requestMap) {
        String userEmail = requestMap.get("email");
        logger.info("Received request to get salt for traveller with email: {}", userEmail);

        try {
            String salt = travellerService.getSalt(userEmail);
            logger.info("Successfully retrieved salt for traveller with email: {}", userEmail);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Salt retrieved", salt));
        }
        catch (Exception e) {
            logger.error("Error retrieving salt for traveller with email {}: {}", userEmail, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Failed to retrieve salt", null));
        }
    }

    //get popular places for dashboard
    @GetMapping("/popular-places/{travellerId}")
    public ResponseEntity<ApiResponse<List<DashboardPlaceDTO>>> getPopularPlaces(@PathVariable Long travellerId) {
        logger.info("Received request to get popular places for dashboard");

        try {
            ApiResponse<List<DashboardPlaceDTO>> response = travellerService.getPopularPlaces(travellerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while getting popular places for dashboard", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while getting popular places for dashboard", null));
        }
    }


    //get favourite places for dashboard
    @GetMapping("/favourite-places/{travellerId}")
    public ResponseEntity<ApiResponse<List<DashboardPlaceDTO>>> getFavouritePlaces(@PathVariable Long travellerId) {
        logger.info("Received request to get favourite places for dashboard");

        try {
            ApiResponse<List<DashboardPlaceDTO>> response = travellerService.getFavouritePlaces(travellerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while getting favourite places for dashboard", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while getting favourite places for dashboard", null));
        }
    }

    @GetMapping("/all-places/{travellerId}")
    public ResponseEntity<ApiResponse<List<DashboardPlaceDTO>>> getAllPlacesForTraveller(@PathVariable Long travellerId) {
        try {
            ApiResponse<List<DashboardPlaceDTO>> response = travellerService.getPlacesForTraveller(travellerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while getting all places for traveller with ID: {}", travellerId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while getting all places for traveller"));
        }
    }

    //logout traveller
    @GetMapping("/logout/{travellerId}")
    public ResponseEntity<ApiResponse<Void>> logout(@PathVariable Long travellerId) {
        logger.info("Received request to logout traveller with ID: {}", travellerId);
        try {
            //delete jwt token
            travellerService.logout(travellerId);
            logger.info("Successfully logged out traveller with ID: {}", travellerId);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Traveller logged out successfully"));
        }
        catch (Exception e) {
            logger.error("Error logging out traveller with ID {}: {}", travellerId, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Failed to logout traveller"));
        }
    }
}
