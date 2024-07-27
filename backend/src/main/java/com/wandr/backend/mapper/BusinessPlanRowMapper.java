package com.wandr.backend.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandr.backend.entity.BusinessPlan;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BusinessPlanRowMapper implements RowMapper<BusinessPlan> {

    private final ObjectMapper objectMapper;

    public BusinessPlanRowMapper() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public BusinessPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
        BusinessPlan businessPlan = new BusinessPlan();
        businessPlan.setPlanId(rs.getLong("plan_id"));
        businessPlan.setName(rs.getString("name"));
        businessPlan.setDescription(rs.getString("description"));
        businessPlan.setPrice(rs.getBigDecimal("price"));
        String featuresJson = rs.getString("features");
        try {
            List<String> features = objectMapper.readValue(featuresJson, List.class);
            businessPlan.setFeatures(features);
        } catch (Exception e) {
            throw new SQLException("Error parsing features JSON", e);
        }
        return businessPlan;
    }
}
