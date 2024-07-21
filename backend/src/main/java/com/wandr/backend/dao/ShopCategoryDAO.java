package com.wandr.backend.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ShopCategoryDAO {

    private final JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(StatisticsDAO.class);

    public ShopCategoryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public String findNameById(int id) {
        String sql = "SELECT name FROM shop_categories WHERE category_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
    }
}
