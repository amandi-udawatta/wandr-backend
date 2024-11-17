package com.wandr.backend.service.impl;

import com.wandr.backend.dao.AdminStatsDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.admin.CountryStatisticsDTO;
import com.wandr.backend.dto.admin.RevenueDTO;
import com.wandr.backend.dto.admin.StatisticsDTO;
import com.wandr.backend.service.AdminStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    private final AdminStatsDAO statisticsDAO;

    private static final Logger logger = LoggerFactory.getLogger(AdminStatsServiceImpl.class);

    @Autowired
    public AdminStatsServiceImpl(AdminStatsDAO statisticsDAO) {
        this.statisticsDAO = statisticsDAO;
    }
    @Override
    public ApiResponse<StatisticsDTO> getStatistics() {
        long totalBusinesses = statisticsDAO.getTotalBusinesses();
        long totalReservations = statisticsDAO.getTotalReservations();
        long premiumAccounts = statisticsDAO.getPremiumAccounts();
        long totalTrips = statisticsDAO.getTotalTrips();

        StatisticsDTO statistics = new StatisticsDTO();
        statistics.setTotalBusinesses(totalBusinesses);
        statistics.setTotalReservations(totalReservations);
        statistics.setTotalAppDownloads(0); // Set to zero for now
        statistics.setPremiumAccounts(premiumAccounts);
        statistics.setTotalTrips(totalTrips);

        return new ApiResponse<>(true, 200, "Statistics retrieved", statistics);
    }

    @Override
    //get a LIST of country statistics dto inside api response data
    public ApiResponse<List<CountryStatisticsDTO>> getUserCountryStatistics() {
        List<CountryStatisticsDTO> countryStatistics = statisticsDAO.getUserCountryStatistics();
        return new ApiResponse<>(true, 200, "User country statistics retrieved successfully", countryStatistics);
    }

    @Override
    public ApiResponse<RevenueDTO> getTotalRevenue() {
        BigDecimal premiumRevenue = statisticsDAO.getPremiumMembershipRevenue();
        BigDecimal businessPlanRevenue = statisticsDAO.getBusinessPlanRevenue();
        BigDecimal reservationCommission = statisticsDAO.getReservationCommission();
        RevenueDTO revenue = new RevenueDTO();
        revenue.setPremiumRevenue(premiumRevenue);
        revenue.setBusinessPlanRevenue(businessPlanRevenue);
        revenue.setReservationRevenue(reservationCommission);
        return new ApiResponse<>(true, 200, "Revenue retrieved successfully", revenue);
    }


}
