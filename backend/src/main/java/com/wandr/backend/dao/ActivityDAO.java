package com.wandr.backend.dao;

import com.wandr.backend.entity.Activity;
import com.wandr.backend.entity.Category;
import com.wandr.backend.mapper.ActivityRowMapper;
import com.wandr.backend.mapper.CategoryRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ActivityDAO {

    private final JdbcTemplate jdbcTemplate;

    public ActivityDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Activity> findAll() {
        String sql = "SELECT * FROM activities";
        return jdbcTemplate.query(sql, new ActivityRowMapper());
    }

    public Activity findByName(String name) {
        String sql = "SELECT * FROM activities WHERE name = ?";
        List<Activity> activities = jdbcTemplate.query(sql, new Object[]{name}, new ActivityRowMapper());
        return activities.isEmpty() ? null : activities.get(0);
    }

    public List<Activity> findByActivityIds(List<Long> activityIds) {
        if (activityIds.isEmpty()) {
            return List.of();
        }
        String sql = String.format("SELECT * FROM activities WHERE activity_id IN (%s)",
                activityIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        return jdbcTemplate.query(sql, new ActivityRowMapper());
    }
}
