package com.wandr.backend.dao;

import com.wandr.backend.dto.business.PaidBusinessDTO;
import com.wandr.backend.entity.Ad;
import com.wandr.backend.entity.Business;
import com.wandr.backend.mapper.AdRowMapper;
import com.wandr.backend.mapper.BusinessRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

    public void saveAd(Ad ad) {
        String sql = "INSERT INTO ads (business_id, title, description, image, requested_date, status) VALUES (?, ?, ?, ?, ?, 'pending')";
        jdbcTemplate.update(sql, ad.getBusinessId(), ad.getTitle(), ad.getDescription(), ad.getImage(), ad.getRequestedDate());
    }

    public List<Ad> findAdsByBusinessId(Long businessId) {
        String sql = "SELECT * FROM ads WHERE business_id = ?";
        return jdbcTemplate.query(sql, new AdRowMapper(), businessId);
    }

    public long countAdsByBusinessId(Long businessId) {
        String sql = "SELECT COUNT(*) FROM ads WHERE business_id = ? AND status = 'approved'";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{businessId}, Long.class);
        } catch (Exception e) {
            logger.error("Error counting ads for business id: {}", businessId, e);
            return 0;
        }
    }

    //delete ad
    public void deleteAd(Long adId) {
        String sql = "DELETE FROM ads WHERE ad_id = ?";
        jdbcTemplate.update(sql, adId);
    }




}
