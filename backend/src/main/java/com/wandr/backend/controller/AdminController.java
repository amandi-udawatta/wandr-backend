package com.wandr.backend.controller;

import com.wandr.backend.dto.*;
import com.wandr.backend.service.AdminService;
import com.wandr.backend.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDetailsDTO>> login(@RequestBody UserLoginDTO request) {
        logger.info("Received request to login admin with email: {}", request.getEmail());
        try {
            ApiResponse<UserDetailsDTO> response = adminService.loginAdmin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while logging in admin with email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, 500, "An error occurred while logging in admin"));
        }
    }

    @GetMapping("/get-salt")
    public ResponseEntity<ApiResponse<String>> getSalt(@RequestParam Map<String, String> requestMap) {
        String userEmail = requestMap.get("email");
        logger.info("Received request to get salt for admin with email: {}", userEmail);

        try {
            String salt = adminService.getSalt(userEmail);
            logger.info("Successfully retrieved salt for admin with email: {}", userEmail);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Salt retrieved", salt));
        }
        catch (Exception e) {
            logger.error("Error retrieving salt for admin with email {}: {}", userEmail, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Failed to retrieve salt", null));
        }
    }




    @PostMapping("/save-jwt")
    public ResponseEntity<ApiResponse<Void>> saveJwtToken(@RequestBody Map<String, String> requestMap) {
        long adminId = Long.parseLong(requestMap.get("userId"));
        String jwtToken = requestMap.get("jwtToken");
        logger.info("Received request to save JWT token for admin with ID: {}", adminId);

        try {
            adminService.updateAdminJwt(jwtToken, adminId);
            logger.info("Successfully saved JWT token for admin with ID: {}", adminId);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "JWT token saved", null));
        }
        catch (Exception e) {
            logger.error("Error saving JWT token for admin with ID {}: {}", adminId, e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Failed to save JWT token", null));
        }
    }
}
