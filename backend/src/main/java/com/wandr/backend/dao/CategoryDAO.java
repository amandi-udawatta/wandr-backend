package com.wandr.backend.dao;

import com.wandr.backend.entity.Category;
import com.wandr.backend.mapper.CategoryRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDAO {

    private final JdbcTemplate jdbcTemplate;

    public CategoryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> findAll() {
        String sql = "SELECT * FROM categories";
        return jdbcTemplate.query(sql, new CategoryRowMapper());
    }

    public Category findByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";
        List<Category> categories = jdbcTemplate.query(sql, new Object[]{name}, new CategoryRowMapper());
        return categories.isEmpty() ? null : categories.get(0);
    }
}
