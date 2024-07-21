package com.wandr.backend.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BusinessPlanDAO {

    private final JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(StatisticsDAO.class);

    public BusinessPlanDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public String findNameById(int id) {
        String sql = "SELECT name FROM business_plan WHERE plan_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("No plan found for plan_id: {}", id);
            return null;
        }
    }
}
