package com.wandr.backend.dao;

import com.wandr.backend.entity.Category;
import com.wandr.backend.entity.Places;
import com.wandr.backend.mapper.CategoryRowMapper;
import com.wandr.backend.mapper.PlaceRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<Category> findByCategoryIds(List<Long> categoryIds) {
        if (categoryIds.isEmpty()) {
            return List.of();
        }
        String sql = String.format("SELECT * FROM categories WHERE category_id IN (%s)",
                categoryIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        return jdbcTemplate.query(sql, new CategoryRowMapper());
    }
}
