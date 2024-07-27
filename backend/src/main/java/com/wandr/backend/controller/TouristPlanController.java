package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.traveller.NewTouristPlanDTO;
import com.wandr.backend.dto.traveller.TouristPlanDTO;
import com.wandr.backend.service.TouristPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tourist-plans")
public class TouristPlanController {

        private final TouristPlanService touristPlanService;
        private static final Logger logger = LoggerFactory.getLogger(TouristPlanController.class);

        @Autowired
        public TouristPlanController(TouristPlanService touristPlanService) {
            this.touristPlanService = touristPlanService;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<TouristPlanDTO>>> getTouristPlans() {
            try {
                return ResponseEntity.ok(touristPlanService.getTouristPlans());
            } catch (Exception e) {
                logger.error("Error getting tourist plans", e);
                return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error getting tourist plans", null));
            }
        }

        @PostMapping("/create")
        public ResponseEntity<ApiResponse<TouristPlanDTO>> createTouristPlan(@RequestBody NewTouristPlanDTO touristPlan) {
            try {
                return ResponseEntity.ok(touristPlanService.create(touristPlan));
            } catch (Exception e) {
                logger.error("Error creating tourist plan", e);
                return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error creating tourist plan", null));
            }
        }

        @PostMapping("/update/{touristPlanId}")
        public ResponseEntity<ApiResponse<TouristPlanDTO>> updateTouristPlan(@PathVariable Long touristPlanId, @RequestBody NewTouristPlanDTO request) {
            try {
                return ResponseEntity.ok(touristPlanService.update(touristPlanId, request));
            } catch (Exception e) {
                logger.error("Error updating tourist plan", e);
                return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error updating tourist plan", null));
            }
        }

        @DeleteMapping("/delete/{touristPlanId}")
        public ResponseEntity<ApiResponse<Void>> deleteTouristPlan(@PathVariable Long touristPlanId) {
            try {
                return ResponseEntity.ok(touristPlanService.delete(touristPlanId));
            } catch (Exception e) {
                logger.error("Error deleting tourist plan", e);
                return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error deleting tourist plan", null));
            }
        }


}

