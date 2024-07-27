package com.wandr.backend.dao;

import com.wandr.backend.dto.business.PaidBusinessDTO;
import com.wandr.backend.entity.Ad;
import com.wandr.backend.entity.Business;
import com.wandr.backend.mapper.AdRowMapper;
import com.wandr.backend.mapper.BusinessRowMapper;
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

    public Ad findById(Long adId) {
        String sql = "SELECT * FROM ads WHERE ad_id = ?";
        return jdbcTemplate.queryForObject(sql, new AdRowMapper(), adId);
    }

    public List<Ad> getPendingAds() {
        String sql = "SELECT * FROM ads WHERE status = 'pending'";
        return jdbcTemplate.query(sql, new AdRowMapper());
    }

    public List<Ad> getApprovedAds() {
        String sql = "SELECT * FROM ads WHERE status = 'approved'";
        return jdbcTemplate.query(sql, new AdRowMapper());
    }

    public void setStatus(Long adId, String status) {
        String sql = "UPDATE ads SET status = ? WHERE ad_id = ?";
        jdbcTemplate.update(sql, status, adId);
    }

}
