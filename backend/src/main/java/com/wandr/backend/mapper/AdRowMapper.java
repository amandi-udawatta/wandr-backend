package com.wandr.backend.mapper;

import com.wandr.backend.entity.Ad;
import com.wandr.backend.entity.Business;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdRowMapper implements RowMapper<Ad> {

    Logger logger = LoggerFactory.getLogger(AdRowMapper.class);

    @Override
    public Ad mapRow(ResultSet rs, int rowNum) throws SQLException {
        Ad ad = new Ad();
        ad.setAdId(rs.getLong("ad_id"));
        ad.setBusinessId(rs.getLong("business_id"));
        ad.setTitle(rs.getString("title"));
        ad.setDescription(rs.getString("description"));
        ad.setImage(rs.getString("image"));
        ad.setRequestedDate(rs.getTimestamp("requested_date"));
        ad.setAdStartDate(rs.getTimestamp("ad_start_date"));
        ad.setAdExpirationDate(rs.getTimestamp("ad_expiration_date"));
        ad.setActive(rs.getBoolean("is_active"));
        ad.setStatus(rs.getString("status"));
        ad.setClickCount(rs.getInt("click_count"));
        ad.setViewCount(rs.getInt("view_count"));
        return ad;
    }
}
