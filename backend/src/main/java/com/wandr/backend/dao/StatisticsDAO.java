package com.wandr.backend.dao;

import com.wandr.backend.dto.statistics.CountryStatisticsDTO;
import com.wandr.backend.entity.Business;
import com.wandr.backend.mapper.BusinessRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class StatisticsDAO {

    private final JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(StatisticsDAO.class);



    public StatisticsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long getTotalBusinesses() {
        String sql = "SELECT COUNT(*) FROM businesses WHERE status != 'declined' AND status != 'pending'";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long getTotalReservations() {
        String sql = "SELECT COUNT(*) FROM reservations";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long getPremiumAccounts() {
        String sql = "SELECT COUNT(*) FROM travellers WHERE membership = 'premium'";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long getTotalTrips() {
        String sql = "SELECT COUNT(*) FROM trips";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public List<CountryStatisticsDTO> getUserCountryStatistics() {
        String sql = "SELECT country, COUNT(*) as userCount FROM travellers GROUP BY country";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CountryStatisticsDTO countryStatisticsDTO = new CountryStatisticsDTO();
            countryStatisticsDTO.setCountry(rs.getString("country"));
            countryStatisticsDTO.setUserCount(rs.getLong("userCount"));
            return countryStatisticsDTO;
        });
    }

    public BigDecimal getPremiumMembershipRevenue() {
        String sql = "SELECT SUM(tp.price) AS premium_membership_revenue " +
                "FROM travellers t " +
                "JOIN tourist_plan tp ON t.membership = 'premium' AND tp.plan_id = 1";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    public BigDecimal getBusinessPlanRevenue() {
        String sql = "SELECT SUM(bp.price) AS business_plan_revenue " +
                "FROM businesses b " +
                "JOIN business_plan bp ON b.plan_id = bp.plan_id";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    public BigDecimal getReservationCommission() {
        String sql = "SELECT SUM(p.price * 0.10) AS reservation_commission " +
                "FROM reservations r " +
                "JOIN product_units pu ON r.reservation_id = pu.reservation_id " +
                "JOIN products p ON pu.product_id = p.product_id " +
                "WHERE pu.reservation_status = 'reserved'";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

}
