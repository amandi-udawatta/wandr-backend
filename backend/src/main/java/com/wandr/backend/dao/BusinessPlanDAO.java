package com.wandr.backend.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandr.backend.entity.BusinessPlan;
import com.wandr.backend.mapper.BusinessPlanRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BusinessPlanDAO {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(StatisticsDAO.class);

    public BusinessPlanDAO(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;

    }

    //find a business plan by id
    public BusinessPlan findById(long id) {
        String sql = "SELECT * FROM business_plan WHERE plan_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BusinessPlanRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.warn("No plan found for plan_id: {}", id);
            return null;
        }
    }
    public String findNameById(long id) {
        String sql = "SELECT name FROM business_plan WHERE plan_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("No plan found for plan_id: {}", id);
            return null;
        }
    }

    //get all business plans
    public List<BusinessPlan> getBusinessPlans() {
        String sql = "SELECT * FROM business_plan";
        return jdbcTemplate.query(sql, new BusinessPlanRowMapper());
    }

    public void createBusinessPlan(BusinessPlan businessPlan) {
        String sql = "INSERT INTO business_plan (name, description, features, price) VALUES (?, ?, ?::jsonb, ?)";
        try {
            String featuresJson = objectMapper.writeValueAsString(businessPlan.getFeatures());
            jdbcTemplate.update(sql, businessPlan.getName(), businessPlan.getDescription(), featuresJson, businessPlan.getPrice());
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON for features", e);
            throw new RuntimeException(e);
        }
    }

    public void updateBusinessPlan(BusinessPlan businessPlan) {
        String sql = "UPDATE business_plan SET name = ?, description = ?, features = ?::jsonb, price = ? WHERE plan_id = ?";
        try {
            String featuresJson = objectMapper.writeValueAsString(businessPlan.getFeatures());
            jdbcTemplate.update(sql, businessPlan.getName(), businessPlan.getDescription(), featuresJson, businessPlan.getPrice(), businessPlan.getPlanId());
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON for features", e);
            throw new RuntimeException(e);
        }

    }

    public void deleteBusinessPlan(long id) {
        String sql = "DELETE FROM business_plan WHERE plan_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
