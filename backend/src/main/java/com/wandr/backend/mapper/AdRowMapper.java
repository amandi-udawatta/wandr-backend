package com.wandr.backend.mapper;

import com.wandr.backend.entity.Ad;
import com.wandr.backend.entity.Business;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AdRowMapper implements RowMapper<Ad> {

    @Override
    public Ad mapRow(ResultSet rs, int rowNum) throws SQLException {
        Ad ad = new Ad();
        ad.setAdId(rs.getLong("ad_id"));
        ad.setBusinessId(rs.getLong("business_id"));
        ad.setTitle(rs.getString("title"));
        ad.setDescription(rs.getString("description"));
        ad.setImage(rs.getString("image"));
        ad.setRequestedDate(rs.getTimestamp("requested_date"));
        ad.setStatus(rs.getString("status"));
        return ad;
    }
}
