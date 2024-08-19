package com.wandr.backend.controller;

import com.wandr.backend.dto.*;
import com.wandr.backend.dto.business.*;
import com.wandr.backend.dto.BusinessRatingDTO;
import com.wandr.backend.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    private final BusinessService businessService;
    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDetailsDTO>> login(@RequestBody UserLoginDTO request) {
        logger.info("Received request to login business with email: {}", request.getEmail());
        try {
            ApiResponse<UserDetailsDTO> response = businessService.loginBusiness(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while logging in business with email: {}", request.getEmail(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while logging in business"));
        }
    }


    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<UserDetailsDTO>> signup(@ModelAttribute BusinessSignupDTO request,
            @RequestParam("shopImage") MultipartFile shopImage, @RequestParam(value = "shopCategory", required = false) Integer shopCategory
    ) {
        System.out.println("languages at controller: " + request.getLanguages());
        logger.info("Received request to register business with email: {}", request.getEmail());

        try{
            ApiResponse<UserDetailsDTO> response = businessService.registerBusiness(request, shopImage, shopCategory);
            logger.info("Successfully registered business with email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while registering business with email: {}", request.getEmail(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while registering business"));
        }
    }


    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<String>> updateProfile( @RequestParam(value = "businessId") Long businessId, @ModelAttribute UpdateProfileDTO request, @RequestParam(value = "shopImage", required = false) MultipartFile shopImage, @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
        logger.info("Received request to update profile for business with ID: {}", businessId);
        try {
            ApiResponse<String> response = businessService.updateProfile(businessId, request, shopImage, profileImage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while updating profile for business with ID: {}", businessId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating profile"));
        }
    }

    @PostMapping("/save-jwt")
    public ResponseEntity<ApiResponse<Void>> saveJwtToken(@RequestBody Map<String, String> requestMap) {
        long businessId = Long.parseLong(requestMap.get("userId"));
        String jwtToken = requestMap.get("jwtToken");
        logger.info("Received request to save JWT token for business with ID: {}", businessId);

        try {
            businessService.updateBusinessJwt(jwtToken, businessId);
            logger.info("Successfully saved JWT token for business with ID: {}", businessId);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "JWT token saved", null));
        }
        catch (Exception e) {
            logger.error("Error saving JWT token for business with ID {}: {}", businessId, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Failed to save JWT token", null));
        }
    }

    @GetMapping("/get-salt")
    public ResponseEntity<ApiResponse<String>> getSalt(@RequestParam Map<String, String> requestMap) {
        String userEmail = requestMap.get("email");
        logger.info("Received request to get salt for business with email: {}", userEmail);

        try {
            String salt = businessService.getSalt(userEmail);
            if(salt == null) {
                logger.error("No business found with email: {}", userEmail);
                return ResponseEntity.ok(new ApiResponse<>(false, 404, "Business not found", null));
            }
            logger.info("Successfully retrieved salt for business with email: {}", userEmail);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Salt retrieved", salt));
        }
        catch (Exception e) {
            logger.error("Error retrieving salt for business with email {}: {}", userEmail, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Failed to retrieve salt", null));
        }
    }


    //get all pending businesses
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<BusinessDTO>>> getPendingBusinesses() {
        logger.info("Received request to get all pending businesses");
        try {
            ApiResponse<List<BusinessDTO>> response = businessService.getPendingBusinesses();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while getting all pending businesses", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while getting all pending businesses", null));
        }
    }

    //get all approved businesses
    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<BusinessDTO>>> getApprovedBusinesses() {
        logger.info("Received request to get all approved businesses");
        try {
            ApiResponse<List<BusinessDTO>> response = businessService.getApprovedBusinesses();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while getting all approved businesses", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while getting all approved businesses", null));
        }
    }

    //get all paid businesses
    @GetMapping("/paid")
    public ResponseEntity<ApiResponse<List<PaidBusinessDTO>>> getPaidBusinesses() {
        logger.info("Received request to get all paid businesses");
        try {
            ApiResponse<List<PaidBusinessDTO>> response = businessService.getPaidBusinesses();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while getting all paid businesses", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while getting all paid businesses", null));
        }
    }

    @GetMapping("/popular-stores")
    public ResponseEntity<ApiResponse<List<PopularStoreDTO>>> getPopularStores() {
        logger.info("Received request to get popular stores");
        try {
            ApiResponse<List<PopularStoreDTO>> response = businessService.getPopularStores();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while getting popular stores", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, 500, "An error occurred while getting popular stores"));
        }
    }

    @GetMapping("/approve/{businessId}")
    public ResponseEntity<ApiResponse<Void>> approveBusiness(@PathVariable Long businessId) {
        logger.info("Received request to approve business with ID: {}", businessId);
        try {
            ApiResponse<Void> response = businessService.approveBusiness(businessId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while approving business with ID: {}", businessId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while approving business"));
        }
    }

    @GetMapping("/decline/{businessId}")
    public ResponseEntity<ApiResponse<Void>> declineBusiness(@PathVariable Long businessId) {
        logger.info("Received request to decline business with ID: {}", businessId);
        try {
            ApiResponse<Void> response = businessService.declineBusiness(businessId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while declining business with ID: {}", businessId, e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while declining business"));
        }
    }

    //logout BUSINESS
    @GetMapping("/logout/{businessId}")
    public ResponseEntity<ApiResponse<Void>> logout(@PathVariable Long businessId) {
        logger.info("Received request to logout business with ID: {}", businessId);
        try {
            //delete jwt token
            businessService.logout(businessId);
            logger.info("Successfully logged out business with ID: {}", businessId);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Business logged out successfully"));
        }
        catch (Exception e) {
            logger.error("Error logging out business with ID {}: {}", businessId, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Failed to logout business"));
        }
    }

    //rate business
    @PostMapping("/rate-business")
    public ResponseEntity<ApiResponse<Void>> rateBusiness(@RequestBody BusinessRatingDTO rating) {
        logger.info("Received request to rate business with businessId: {}", rating.getBusinessId());
        try {
            ApiResponse<Void> response = businessService.rateBusiness(rating.getTravellerId(), rating.getBusinessId(), rating.getRating());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while rating business with businessId: {}", rating.getBusinessId(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while rating business"));
        }
    }



}
