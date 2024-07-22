package com.wandr.backend.controller;

import com.wandr.backend.dto.*;
import com.wandr.backend.dto.business.BusinessDTO;
import com.wandr.backend.dto.business.BusinessSignupDTO;
import com.wandr.backend.dto.business.PopularStoreDTO;
import com.wandr.backend.dto.business.UpdateProfileDTO;
import com.wandr.backend.service.BusinessService;
import com.wandr.backend.util.FileUploadUtil;
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, 500, "An error occurred while logging in business"));
        }
    }


    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<UserDetailsDTO>> signup(@ModelAttribute BusinessSignupDTO request,
            @RequestParam("shopImage") MultipartFile shopImage, @RequestParam(value = "shopCategory", required = false) Integer shopCategory
    ) {
        logger.info("Received request to register business with email: {}", request.getEmail());

        try{
            ApiResponse<UserDetailsDTO> response = businessService.registerBusiness(request, shopImage, shopCategory);
            logger.info("Successfully registered business with email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while registering business with email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, 500, "An error occurred while registering business"));
        }
    }


    @PutMapping("/{businessId}/profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(@PathVariable Long businessId, @RequestBody UpdateProfileDTO request) {
        logger.info("Received request to update profile for business with ID: {}", businessId);
        try {
            ApiResponse<String> response = businessService.updateProfile(businessId, request);
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
}
