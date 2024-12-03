package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.business.BusinessStatisticsDTO;
import com.wandr.backend.dto.business.TopProductDTO;
import com.wandr.backend.dto.business.OngoingAdvertisementDTO;
import com.wandr.backend.service.BusinessStatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business/stats")
public class BusinessStatController {

    private final BusinessStatService businessStatService;

    public BusinessStatController(BusinessStatService businessStatService) {
        this.businessStatService = businessStatService;
    }

    @GetMapping("/{businessId}/statistics")
    public ApiResponse<BusinessStatisticsDTO> getBusinessStatistics(@PathVariable Long businessId) {
        return businessStatService.getBusinessStatistics(businessId);
    }

    @GetMapping("/{businessId}/ongoing-ads")
    public ApiResponse<List<OngoingAdvertisementDTO>> getOngoingAdvertisements(@PathVariable Long businessId) {
        return businessStatService.getOngoingAdvertisements(businessId);
    }

    @GetMapping("/{businessId}/top-products")
    public ApiResponse<List<TopProductDTO>> getTopProducts(@PathVariable Long businessId) {
        return businessStatService.getTopProducts(businessId);
    }
}
