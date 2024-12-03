package com.wandr.backend.service.impl;

import com.wandr.backend.dao.BusinessStatDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.business.BusinessStatisticsDTO;
import com.wandr.backend.dto.business.OngoingAdvertisementDTO;
import com.wandr.backend.dto.business.TopProductDTO;
import com.wandr.backend.service.BusinessStatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessStatServiceImpl implements BusinessStatService {

    private final BusinessStatDAO businessStatDAO;

    public BusinessStatServiceImpl(BusinessStatDAO businessStatDAO) {
        this.businessStatDAO = businessStatDAO;
    }

    @Override
    public ApiResponse<BusinessStatisticsDTO> getBusinessStatistics(Long businessId) {
        BusinessStatisticsDTO statistics = businessStatDAO.getBusinessStatistics(businessId);
        if (statistics != null) {
            return new ApiResponse<>(true, 200, "Business statistics retrieved successfully", statistics);
        } else {
            return new ApiResponse<>(false, 404, "Business statistics not found");
        }
    }

    @Override
    public ApiResponse<List<OngoingAdvertisementDTO>> getOngoingAdvertisements(Long businessId) {
        List<OngoingAdvertisementDTO> ads = businessStatDAO.getOngoingAdvertisements(businessId);
        if (!ads.isEmpty()) {
            return new ApiResponse<>(true, 200, "Ongoing advertisements retrieved successfully", ads);
        } else {
            return new ApiResponse<>(true, 200, "No ongoing advertisements found", List.of());
        }
    }

    @Override
    public ApiResponse<List<TopProductDTO>> getTopProducts(Long businessId) {
        List<TopProductDTO> topProducts = businessStatDAO.getTopProducts(businessId);
        if (!topProducts.isEmpty()) {
            return new ApiResponse<>(true, 200, "Top products retrieved successfully", topProducts);
        } else {
            return new ApiResponse<>(true, 200, "No products found for the business", List.of());
        }
    }
}

