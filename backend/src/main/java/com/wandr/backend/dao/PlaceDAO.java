package com.wandr.backend.dao;

import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.entity.Places;
import com.wandr.backend.mapper.DashboardPlacesRowMapper;
import com.wandr.backend.mapper.PlaceRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PlaceDAO {

    private final JdbcTemplate jdbcTemplate;

    private final CategoryDAO categoryDAO;
    private final ActivityDAO activityDAO;

    public PlaceDAO(JdbcTemplate jdbcTemplate, CategoryDAO categoryDAO, ActivityDAO activityDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.categoryDAO = categoryDAO;
        this.activityDAO = activityDAO;
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

    public boolean isPlaceLikedByTraveller(Long placeId, Long travellerId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE place_id = ? AND traveller_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{placeId, travellerId}, Integer.class);
        return count != null && count > 0;
    }

    public String getLatLongByPlaceId(Long placeId) {
        String sql = "SELECT latitude, longitude FROM places WHERE place_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{placeId}, (rs, rowNum) ->
                rs.getDouble("latitude") + "," + rs.getDouble("longitude")
        );
    }

    public List<DashboardPlaceDTO> getDashboardPlacesByIds(List<Long> placeIds, Long travellerId) {
        //handle empty ones too
        if (placeIds.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT p.*, " +
                "EXISTS (SELECT 1 FROM likes l WHERE l.place_id = p.place_id AND l.traveller_id = ?) AS liked " +
                "FROM places p WHERE p.place_id IN (" + String.join(",", placeIds.stream().map(String::valueOf).collect(Collectors.toList())) + ")";
        return jdbcTemplate.query(sql, new Object[]{travellerId}, new DashboardPlacesRowMapper(categoryDAO, activityDAO));
    }

}

