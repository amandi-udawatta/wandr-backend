//package com.wandr.backend.dao;
//
//import com.wandr.backend.dto.admin.CountryStatisticsDTO;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.YearMonth;
//import java.util.List;
//
//@Repository
//public class BusinessStatsDAO {
//
//    private final JdbcTemplate jdbcTemplate;
//    Logger logger = LoggerFactory.getLogger(BusinessStatsDAO.class);
//
//
//
//    public BusinessStatsDAO(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public int getTotalProductsSold(Long businessId, int month) {
//        String sql = "SELECT COUNT(*) FROM sales " +
//                "WHERE business_id = ? AND EXTRACT(MONTH FROM sale_date) = ? AND EXTRACT(YEAR FROM sale_date) = ?";
//        return jdbcTemplate.queryForObject(sql, new Object[]{businessId, month, YearMonth.now().getYear()}, Integer.class);
//    }
//
//    public double getTotalRevenue(Long businessId, int month) {
//        String sql = "SELECT COALESCE(SUM(price), 0) FROM sales " +
//                "WHERE business_id = ? AND EXTRACT(MONTH FROM sale_date) = ? AND EXTRACT(YEAR FROM sale_date) = ?";
//        return jdbcTemplate.queryForObject(sql, new Object[]{businessId, month, YearMonth.now().getYear()}, Double.class);
//    }
//
//    public int getPositiveFeedbackCount(Long businessId) {
//        String sql = "SELECT COUNT(*) FROM businesses " +
//                "WHERE business_id = ? AND rating >= 3"; // Assuming 3+ is positive feedback
//        return jdbcTemplate.queryForObject(sql, new Object[]{businessId}, Integer.class);
//    }
//
//    public int getNegativeFeedbackCount(Long businessId) {
//        String sql = "SELECT COUNT(*) FROM businesses " +
//                "WHERE business_id = ? AND rating <= 2"; // Assuming 2 or less is negative feedback
//        return jdbcTemplate.queryForObject(sql, new Object[]{businessId}, Integer.class);
//    }
//
//    public LocalDate getPlanEndDate(Long businessId) {
//        String sql = "SELECT plan_end_date FROM business_plans " +
//                "WHERE business_id = ?";
//        return jdbcTemplate.queryForObject(sql, new Object[]{businessId}, LocalDate.class);
//    }
//
//}
