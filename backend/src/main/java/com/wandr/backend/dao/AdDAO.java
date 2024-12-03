package com.wandr.backend.dao;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.ads.AdDTO;
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

import java.util.ArrayList;
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

   public void updateAd(Ad ad) {
    String sql = "UPDATE ads SET title = ?, description = ?, image = ?, ad_start_date = ?, ad_expiration_date = ?, is_active = ?, view_count = ?, click_count = ? WHERE ad_id = ?";
    jdbcTemplate.update(sql, ad.getTitle(), ad.getDescription(), ad.getImage(), ad.getAdStartDate(), ad.getAdExpirationDate(), ad.isActive(), ad.getViewCount(), ad.getClickCount(), ad.getAdId());
}

    public List<Ad> getAdsByBusinessId(Long businessId) {
        String sql = "SELECT * FROM ads WHERE business_id = ?";
        return jdbcTemplate.query(sql, new AdRowMapper(), businessId);
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


    //delete ad
    public void deleteAd(Long adId) {
        String sql = "DELETE FROM ads WHERE ad_id = ?";
        jdbcTemplate.update(sql, adId);
    }

    public List<Ad> findExpiredAds() {
        String sql = "SELECT * FROM ads WHERE ad_expiration_date <= NOW() AND is_active = TRUE";
        return jdbcTemplate.query(sql, new AdRowMapper());
    }

    public long countAdsByBusinessId(Long businessId) {
        String sql = "SELECT COUNT(*) FROM ads WHERE business_id = ? AND is_active = TRUE";
        return jdbcTemplate.queryForObject(sql, Long.class, businessId);
    }






}
