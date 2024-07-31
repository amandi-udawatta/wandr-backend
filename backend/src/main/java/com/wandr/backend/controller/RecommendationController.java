package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.recommendation.RecommendedPlaceDTO;
import com.wandr.backend.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{travellerId}")
    public ResponseEntity<ApiResponse<List<RecommendedPlaceDTO>>> getRecommendations(@PathVariable Long travellerId) {
        List<RecommendedPlaceDTO> recommendations = recommendationService.getRecommendedPlaces(travellerId);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Recommendations retrieved successfully", recommendations));
    }
}
