package com.wandr.backend.dao;

import com.wandr.backend.entity.Ad;
import com.wandr.backend.mapper.AdRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class AdDAO {

    private final JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(AdDAO.class);

    public AdDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ad> getPendingAds() {
        String sql = "SELECT * FROM ads WHERE status = 'pending'";
        return jdbcTemplate.query(sql, new AdRowMapper());
    }

}
