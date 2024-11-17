//package com.wandr.backend.controller;
//
//import com.wandr.backend.dto.ApiResponse;
//import com.wandr.backend.dto.Business.CountryStatisticsDTO;
//import com.wandr.backend.dto.Business.RevenueDTO;
//import com.wandr.backend.dto.Business.StatisticsDTO;
//import com.wandr.backend.service.BusinessStatsService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/business/statistics")
//public class BusinessStatsController {
//
//    private final BusinessStatsService statisticsService;
//    private static final Logger logger = LoggerFactory.getLogger(BusinessStatsController.class);
//
//    @Autowired
//    public BusinessStatsController(BusinessStatsService statisticsService) {
//        this.statisticsService = statisticsService;
//    }
//
//    @GetMapping("/dashboard/{businessId}")
//    public ResponseEntity<ApiResponse<StatisticsDTO>> getStatistics(@PathVariable Long businessId) {
//        try{
//            return ResponseEntity.ok(statisticsService.getStatistics());
//        } catch (Exception e) {
//            logger.error("Error getting statistics", e);
//            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Error getting statistics", null));
//        }
//    }
//
//
//
//}
