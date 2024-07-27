package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.business.BusinessPlanDTO;
import com.wandr.backend.service.BusinessPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-plans")
public class BusinessPlanController {

    private final BusinessPlanService businessPlanService;
    private static final Logger logger = LoggerFactory.getLogger(BusinessPlanController.class);

    @Autowired
    public BusinessPlanController(BusinessPlanService businessPlanService) {
        this.businessPlanService = businessPlanService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BusinessPlanDTO>>> getBusinessPlans() {
        try {
            return ResponseEntity.ok(businessPlanService.getBusinessPlans());
        } catch (Exception e) {
            logger.error("Error getting business plans", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error getting business plans", null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<BusinessPlanDTO>> createBusinessPlan(@RequestBody BusinessPlanDTO businessPlan) {
        try {
            return ResponseEntity.ok(businessPlanService.create(businessPlan));
        } catch (Exception e) {
            logger.error("Error creating business plan", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error creating business plan", null));
        }
    }

    @PostMapping("/update/{businessPlanId}")
    public ResponseEntity<ApiResponse<BusinessPlanDTO>> updateBusinessPlan(@PathVariable Long businessPlanId, @RequestBody BusinessPlanDTO request) {
        try {
            return ResponseEntity.ok(businessPlanService.update(businessPlanId, request));
        } catch (Exception e) {
            logger.error("Error updating business plan", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error updating business plan", null));
        }
    }

    @DeleteMapping("/delete/{businessPlanId}")
    public ResponseEntity<ApiResponse<Void>> deleteBusinessPlan(@PathVariable Long businessPlanId) {
        try {
            return ResponseEntity.ok(businessPlanService.delete(businessPlanId));
        } catch (Exception e) {
            logger.error("Error deleting business plan", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error deleting business plan", null));
        }
    }
}
