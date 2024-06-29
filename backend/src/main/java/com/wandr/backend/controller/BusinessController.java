//package com.wandr.backend.controller;
//
//import com.wandr.backend.dto.*;
//import com.wandr.backend.service.TravellerService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/business")
//public class BusinessController {
//
//    private final BusinessService businessService;
//    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);
//
//    @Autowired
//    public BusinessController(BusinessService businessService) {
//        this.businessService = businessService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<UserDetailsDTO>> login(@RequestBody UserLoginDTO request) {
//        logger.info("Received request to login business with email: {}", request.getEmail());
//        try {
//            ApiResponse<UserDetailsDTO> response = businessService.loginBusiness(request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("An error occurred while logging in business with email: {}", request.getEmail(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(false, 500, "An error occurred while logging in traveller"));
//        }
//    }
//
//
////    @PostMapping("/signup")
////    public ResponseEntity<ApiResponse<UserDetailsDTO>> signup(@RequestBody TravellerSignupDTO request) {
////        logger.info("Received request to register traveller with email: {}", request.getEmail());
////        try {
////            ApiResponse<UserDetailsDTO> response = travellerService.registerTraveller(request);
////            return ResponseEntity.ok(response);
////        } catch (Exception e) {
////            logger.error("An error occurred while registering traveller with email: {}", request.getEmail(), e);
////            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while registering traveller"));
////        }
////    }
////
////    @PutMapping("/{travellerId}/categories")
////    public ResponseEntity<ApiResponse<String>> updateCategories(@PathVariable Long travellerId, @RequestBody UpdateCategoriesDTO request) {
////        logger.info("Received request to update categories for traveller with ID: {}", travellerId);
////        try {
////            ApiResponse<String> response = travellerService.updateCategories(travellerId, request);
////            logger.info("Categories updated successfully for traveller with ID: {}", travellerId);
////            return ResponseEntity.ok(response);
////        } catch (Exception e) {
////            logger.error("An error occurred while updating categories for traveller with ID: {}", travellerId, e);
////            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating categories"));
////        }
////    }
////
////    @PutMapping("/{travellerId}/activities")
////    public ResponseEntity<ApiResponse<String>> updateActivities(@PathVariable Long travellerId, @RequestBody UpdateActivitiesDTO request) {
////        logger.info("Received request to update activities for traveller with ID: {}", travellerId);
////        try {
////            ApiResponse<String> response = travellerService.updateActivities(travellerId, request);
////            logger.info("Activities updated successfully for traveller with ID: {}", travellerId);
////            return ResponseEntity.ok(response);
////        } catch (Exception e) {
////            logger.error("An error occurred while updating activities for traveller with ID: {}", travellerId, e);
////            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating activities"));
////        }
////    }
////
////    @PutMapping("/{travellerId}/profile")
////    public ResponseEntity<ApiResponse<String>> updateProfile(@PathVariable Long travellerId, @RequestBody UpdateProfileDTO request) {
////        logger.info("Received request to update profile for traveller with ID: {}", travellerId);
////        try {
////            ApiResponse<String> response = travellerService.updateProfile(travellerId, request);
////            return ResponseEntity.ok(response);
////        } catch (Exception e) {
////            logger.error("An error occurred while updating profile for traveller with ID: {}", travellerId, e);
////            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating profile"));
////        }
////    }
//
//    @PostMapping("/save-jwt")
//    public ResponseEntity<ApiResponse<Void>> saveJwtToken(@RequestBody Map<String, String> requestMap) {
//        long businessId = Long.parseLong(requestMap.get("userId"));
//        String jwtToken = requestMap.get("jwtToken");
//        logger.info("Received request to save JWT token for business with ID: {}", businessId);
//
//        try {
//            businessService.updateBusinessJwt(jwtToken, businessId);
//            logger.info("Successfully saved JWT token for business with ID: {}", businessId);
//            return ResponseEntity.ok(new ApiResponse<>(true, 200, "JWT token saved", null));
//        }
//        catch (Exception e) {
//            logger.error("Error saving JWT token for business with ID {}: {}", businessId, e.getMessage(), e);
//            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Failed to save JWT token", null));
//        }
//    }
//}
