package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.statistics.CountryStatisticsDTO;
import com.wandr.backend.dto.statistics.StatisticsDTO;

import java.util.List;

public interface StatisticsService {

    ApiResponse<StatisticsDTO> getStatistics();
    ApiResponse<List<CountryStatisticsDTO>> getUserCountryStatistics();


}
