package com.wandr.backend.dao;

import com.wandr.backend.dto.statistics.CountryStatisticsDTO;
import com.wandr.backend.entity.Business;
import com.wandr.backend.mapper.BusinessRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

    //get businesses count from that is not pending or rejected in status column
    public Integer getBusinessesCount() {
        String sql = "SELECT COUNT(*) FROM businesses WHERE status != 'pending' AND status != 'declined'";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

}
