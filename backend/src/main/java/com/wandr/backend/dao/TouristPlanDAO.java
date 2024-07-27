package com.wandr.backend.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandr.backend.entity.BusinessPlan;
import com.wandr.backend.entity.TouristPlan;
import com.wandr.backend.mapper.BusinessPlanRowMapper;
import com.wandr.backend.mapper.TouristPlanRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TouristPlanDAO {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(StatisticsDAO.class);

    public TouristPlanDAO(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;

    }

    public TouristPlan findById(long id) {
        String sql = "SELECT * FROM tourist_plan WHERE plan_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new TouristPlanRowMapper(objectMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.warn("No plan found for plan_id: {}", id);
            return null;
        }
    }

    public String findNameById(long id) {
        String sql = "SELECT name FROM tourist_plan WHERE plan_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("No plan found for plan_id: {}", id);
            return null;
        }
    }

    //get all tourist plans
    public List<TouristPlan> getTouristPlans() {
        String sql = "SELECT * FROM tourist_plan";
        return jdbcTemplate.query(sql, new TouristPlanRowMapper(objectMapper));
    }

    public void createTouristPlan(TouristPlan touristPlan) {
        String sql = "INSERT INTO tourist_plan (name, description, features, price) VALUES (?, ?, ?::jsonb, ?)";
        try {
            String featuresJson = objectMapper.writeValueAsString(touristPlan.getFeatures());
            jdbcTemplate.update(sql, touristPlan.getName(), touristPlan.getDescription(), featuresJson, touristPlan.getPrice());
        } catch (JsonProcessingException e) {
            logger.error("Error creating tourist plan", e);
        }
    }

    public void updateTouristPlan(TouristPlan touristPlan) {
        String sql = "UPDATE tourist_plan SET name = ?, description = ?, features = ?::jsonb, price = ? WHERE plan_id = ?";
        try {
            String featuresJson = objectMapper.writeValueAsString(touristPlan.getFeatures());
            jdbcTemplate.update(sql, touristPlan.getName(), touristPlan.getDescription(), featuresJson, touristPlan.getPrice(), touristPlan.getPlanId());
        } catch (JsonProcessingException e) {
            logger.error("Error updating tourist plan", e);
        }
    }

    public void deleteTouristPlan(long id) {
        String sql = "DELETE FROM tourist_plan WHERE plan_id = ?";
        jdbcTemplate.update(sql, id);
    }


}
