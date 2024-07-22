package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.statistics.CountryStatisticsDTO;
import com.wandr.backend.dto.statistics.RevenueDTO;
import com.wandr.backend.dto.statistics.StatisticsDTO;
import com.wandr.backend.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<StatisticsDTO>> getStatistics() {
        try{
            return ResponseEntity.ok(statisticsService.getStatistics());
        } catch (Exception e) {
            logger.error("Error getting statistics", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error getting statistics", null));
        }
    }

    @GetMapping("/user-country")
    public ResponseEntity<ApiResponse<List<CountryStatisticsDTO>>> getUserCountryStatistics() {
        try{
            return ResponseEntity.ok(statisticsService.getUserCountryStatistics());
        } catch (Exception e) {
            logger.error("Error getting user country statistics", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error getting user country statistics", null));
        }
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<ApiResponse<RevenueDTO>> getTotalRevenue() {
        try{
            return ResponseEntity.ok(statisticsService.getTotalRevenue());
        } catch (Exception e) {
            logger.error("Error getting total revenue", e);
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error getting total revenue", null));
        }
    }


}
