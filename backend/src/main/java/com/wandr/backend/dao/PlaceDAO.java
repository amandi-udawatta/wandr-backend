package com.wandr.backend.dao;

import com.wandr.backend.entity.Places;
import com.wandr.backend.mapper.PlaceRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.List;

@Repository
public class PlaceDAO {

    private final JdbcTemplate jdbcTemplate;

    public PlaceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Places place) {
        String sql = "INSERT INTO places (name, description, latitude, longitude, address, image) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, place.getName(), place.getDescription(), place.getLatitude(), place.getLongitude(), place.getAddress(), place.getImage());
    }

    public List<Places> findAll() {
        String sql = "SELECT * FROM places";
        return jdbcTemplate.query(sql, new PlaceRowMapper());
    }

    public void updateCategories(Long placeId, List<Long> categories) {
        String sql = "UPDATE places SET categories = ?::jsonb WHERE place_id = ?";
        jdbcTemplate.update(sql, categories.toString(), placeId);
    }

    public void updateActivities(Long placeId, List<Long> activities) {
        String sql = "UPDATE places SET activities = ?::jsonb WHERE place_id = ?";
        jdbcTemplate.update(sql, activities.toString(), placeId);
    }

    public Places findById(Long placeId) {
        String sql = "SELECT * FROM places WHERE place_id = ?";
        List<Places> places = jdbcTemplate.query(sql, new Object[]{placeId}, new PlaceRowMapper());
        return places.isEmpty() ? null : places.get(0);
    }
}
