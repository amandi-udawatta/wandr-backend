package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.business.BusinessStatisticsDTO;
import com.wandr.backend.dto.business.OngoingAdvertisementDTO;
import com.wandr.backend.dto.business.TopProductDTO;

import java.util.List;

public interface BusinessStatService {

    ApiResponse<BusinessStatisticsDTO> getBusinessStatistics(Long businessId);

    ApiResponse<List<OngoingAdvertisementDTO>> getOngoingAdvertisements(Long businessId);

    ApiResponse<List<TopProductDTO>> getTopProducts(Long businessId);
}
