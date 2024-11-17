package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.admin.CountryStatisticsDTO;
import com.wandr.backend.dto.admin.RevenueDTO;
import com.wandr.backend.dto.admin.StatisticsDTO;


import java.util.List;

public interface AdminStatsService {

    ApiResponse<StatisticsDTO> getStatistics();
    ApiResponse<List<CountryStatisticsDTO>> getUserCountryStatistics();
    ApiResponse<RevenueDTO> getTotalRevenue();

}
