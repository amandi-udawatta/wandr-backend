package com.wandr.backend.dao;

import com.wandr.backend.dto.place.Coordinates;
import com.wandr.backend.entity.Places;
import com.wandr.backend.mapper.PlaceRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PlaceDAO {

    private final JdbcTemplate jdbcTemplate;

    public PlaceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Places place) {
        String sql = "INSERT INTO places (name, description, latitude, longitude, address, image) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, place.getName(), place.getDescription(), place.getLatitude(), place.getLongitude(), place.getAddress(), place.getImage());
        return jdbcTemplate.queryForObject("SELECT lastval()", Long.class);
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

    public void update(Places place) {
        String sql = "UPDATE places SET name = ?, description = ?, address = ? WHERE place_id = ?";
        System.out.println(sql);
        jdbcTemplate.update(sql, place.getName(), place.getDescription(), place.getAddress(), place.getId());
    }

    public void updateDescription(Long placeId, String description) {
        String sql = "UPDATE places SET description = ? WHERE place_id = ?";
        jdbcTemplate.update(sql, description, placeId);
    }

    public void delete(Long placeId) {
        String sql = "DELETE FROM places WHERE place_id = ?";
        jdbcTemplate.update(sql, placeId);
    }

    // Method to get place id from coordinate
    public Long getPlaceIdFromCoordinate(String coordinates) {
        String sql = "SELECT place_id FROM places WHERE latitude = ? AND longitude = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{coordinates.split(",")[0], coordinates.split(",")[1]}, Long.class);
    }


}
