package com.wandr.backend.controller;

import com.wandr.backend.dto.*;
import com.wandr.backend.service.TravellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

//    private final AdminService adminService;
//    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
//
//    @Autowired
//    public AdminController(AdminService adminService) {
//        this.adminService = travellerService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<String>> login(@RequestBody AdminLoginDTO request) {
//        logger.info("Received request to login admin with email: {}", request.getEmail());
//        try {
//            ApiResponse<String> response = adminService.loginAdmin(request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("An error occurred while log in traveller with email: {}", request.getEmail(), e);
//            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while log in traveller"));
//        }
//    }
//
//    @PostMapping("/signup")
//    public ResponseEntity<ApiResponse<String>> signup(@RequestBody TravellerSignupDTO request) {
//        logger.info("Received request to register traveller with email: {}", request.getEmail());
//        try {
//            ApiResponse<String> response = adminService.registerTraveller(request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("An error occurred while registering traveller with email: {}", request.getEmail(), e);
//            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while registering traveller"));
//        }
//    }
//
//    @PutMapping("/{travellerId}/categories")
//    public ResponseEntity<ApiResponse<String>> updateCategories(@PathVariable Long travellerId, @RequestBody UpdateCategoriesDTO request) {
//        logger.info("Received request to update categories for traveller with ID: {}", travellerId);
//        try {
//            ApiResponse<String> response = adminService.updateCategories(travellerId, request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("An error occurred while updating categories for traveller with ID: {}", travellerId, e);
//            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating categories"));
//        }
//    }
//
//    @PutMapping("/{travellerId}/activities")
//    public ResponseEntity<ApiResponse<String>> updateActivities(@PathVariable Long travellerId, @RequestBody UpdateActivitiesDTO request) {
//        logger.info("Received request to update activities for traveller with ID: {}", travellerId);
//        try {
//            ApiResponse<String> response = adminService.updateActivities(travellerId, request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("An error occurred while updating activities for traveller with ID: {}", travellerId, e);
//            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating activities"));
//        }
//    }
//
//    @PutMapping("/{travellerId}/profile")
//    public ResponseEntity<ApiResponse<String>> updateProfile(@PathVariable Long travellerId, @RequestBody UpdateProfileDTO request) {
//        logger.info("Received request to update profile for traveller with ID: {}", travellerId);
//        try {
//            ApiResponse<String> response = adminService.updateProfile(travellerId, request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("An error occurred while updating profile for traveller with ID: {}", travellerId, e);
//            return ResponseEntity.ok(new ApiResponse<>(false, 500, "An error occurred while updating profile"));
//        }
//    }
}
