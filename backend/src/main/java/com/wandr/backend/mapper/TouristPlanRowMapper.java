package com.wandr.backend.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandr.backend.entity.TouristPlan;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TouristPlanRowMapper implements RowMapper<TouristPlan> {

    private final ObjectMapper objectMapper;

    public TouristPlanRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public TouristPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
        TouristPlan touristPlan = new TouristPlan();
        touristPlan.setPlanId(rs.getLong("plan_id"));
        touristPlan.setName(rs.getString("name"));
        touristPlan.setDescription(rs.getString("description"));
        try {
            List<String> features = objectMapper.readValue(rs.getString("features"), List.class);
            touristPlan.setFeatures(features);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing features", e);
        }
        touristPlan.setPrice(rs.getBigDecimal("price"));
        return touristPlan;
    }
}
