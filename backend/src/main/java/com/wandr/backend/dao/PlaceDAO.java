package com.wandr.backend.dao;

import com.wandr.backend.entity.Activity;
import com.wandr.backend.entity.Category;
import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.entity.Places;
import com.wandr.backend.mapper.PlaceRowMapper;
import com.wandr.backend.util.BoundingBox;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
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

    public List<DashboardPlaceDTO> getDashboardPlacesWithinBoundingBox(List<Long> placeIds, BoundingBox boundingBox, Long travellerId) {
        // Convert the List<Long> to a PostgreSQL array string
        String placeIdsArray = placeIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "{", "}")); // Formats like {1,2,3}

        String sql = " SELECT p.*, EXISTS (SELECT 1 FROM likes l WHERE l.place_id = p.place_id AND l.traveller_id = ?) AS liked" +
                " FROM places p" +
                " WHERE p.place_id = ANY(?::bigint[])" +
                " AND p.latitude BETWEEN ? AND ?" +
                " AND p.longitude BETWEEN ? AND ?";

        return jdbcTemplate.query(
                sql,
                new Object[]{
                        travellerId, // To check if the place is liked by the traveller
                        placeIdsArray, // Pass the PostgreSQL array string
                        boundingBox.getMinLat(),
                        boundingBox.getMaxLat(),
                        boundingBox.getMinLng(),
                        boundingBox.getMaxLng()
                },
                (rs, rowNum) -> {
                    DashboardPlaceDTO dto = new DashboardPlaceDTO();
                    dto.setId(rs.getLong("place_id"));
                    dto.setName(rs.getString("name"));
                    dto.setDescription(rs.getString("description"));
                    dto.setLatitude(rs.getDouble("latitude"));
                    dto.setLongitude(rs.getDouble("longitude"));
                    dto.setAddress(rs.getString("address"));
                    dto.setImage(rs.getString("image"));

                    // Parse categories
                    String categories = rs.getString("categories");
                    if (categories != null && !categories.trim().isEmpty()) {
                        List<Long> categoryIds = Arrays.stream(categories.replaceAll("[\\[\\]\\s]", "").split(","))
                                .filter(str -> !str.isEmpty())
                                .map(Long::parseLong)
                                .collect(Collectors.toList());
                        dto.setCategories(categoryDAO.findByCategoryIds(categoryIds)
                                .stream()
                                .map(Category::getName)
                                .collect(Collectors.toList()));
                    } else {
                        dto.setCategories(Collections.emptyList());
                    }

                    // Parse activities
                    String activities = rs.getString("activities");
                    if (activities != null && !activities.trim().isEmpty()) {
                        List<Long> activityIds = Arrays.stream(activities.replaceAll("[\\[\\]\\s]", "").split(","))
                                .filter(str -> !str.isEmpty())
                                .map(Long::parseLong)
                                .collect(Collectors.toList());
                        dto.setActivities(activityDAO.findByActivityIds(activityIds)
                                .stream()
                                .map(Activity::getName)
                                .collect(Collectors.toList()));
                    } else {
                        dto.setActivities(Collections.emptyList());
                    }

                    dto.setLiked(rs.getBoolean("liked"));
                    dto.setRating(rs.getInt("rating"));
                    return dto;
                }
        );
    }

}

