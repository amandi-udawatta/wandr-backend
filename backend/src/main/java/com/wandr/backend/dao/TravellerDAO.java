package com.wandr.backend.dao;

import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.entity.Activity;
import com.wandr.backend.entity.Category;
import com.wandr.backend.entity.Traveller;
import com.wandr.backend.mapper.TravellerRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TravellerDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CategoryDAO categoryDAO;
    private final ActivityDAO activityDAO;

    public TravellerDAO(JdbcTemplate jdbcTemplate, CategoryDAO categoryDAO, ActivityDAO activityDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.categoryDAO = categoryDAO;
        this.activityDAO = activityDAO;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM travellers WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        return count != null && count > 0;
    }

    public void save(Traveller traveller) {
        String sql = "INSERT INTO travellers (name, email, password, country, categories, activities,salt, created_at) VALUES (?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, ?)";
        jdbcTemplate.update(sql, traveller.getName(), traveller.getEmail(), traveller.getPassword(), traveller.getCountry(), traveller.getCategories().toString(), traveller.getActivities().toString(), traveller.getSalt(), traveller.getCreatedAt());
    }

    public void updateCategories(Long travellerId, List<Integer> categories) {
        String sql = "UPDATE travellers SET categories = ?::jsonb WHERE traveller_id = ?";
        jdbcTemplate.update(sql, categories.toString(), travellerId);
    }

    public void updateActivities(Long travellerId, List<Integer> activities) {
        String sql = "UPDATE travellers SET activities = ?::jsonb WHERE traveller_id = ?";
        jdbcTemplate.update(sql, activities.toString(), travellerId);
    }
    public Traveller findById(Long travellerId) {
        String sql = "SELECT * FROM travellers WHERE traveller_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{travellerId}, new TravellerRowMapper());
    }

    public void updateProfile(Traveller traveller) {
        String sql = "UPDATE travellers SET name = ?, country = ?, categories = ?::jsonb, activities = ?::jsonb, profile_image = ?, membership = ? WHERE traveller_id = ?";
        jdbcTemplate.update(sql, traveller.getName(), traveller.getCountry(), traveller.getCategories().toString(), traveller.getActivities().toString(), traveller.getProfileImage(),traveller.getMembership(), traveller.getTravellerId());
    }

    public Optional<Traveller> findByEmail(String email) {
        String sql = "SELECT * FROM travellers WHERE email = ?";
        List<Traveller> travellers = jdbcTemplate.query(sql, new Object[]{email}, new TravellerRowMapper());
        return travellers.isEmpty() ? Optional.empty() : Optional.of(travellers.get(0));
    }

    public void updateTravellerJwt(String jwt, Long travellerId) {
        String sql = "UPDATE travellers SET jwt = ? WHERE traveller_id = ?";
        jdbcTemplate.update(sql, jwt, travellerId);
    }


    //get popular places
    public List<DashboardPlaceDTO> getPopularPlaces(Long travellerId) {
        String sql = "SELECT p.*, " +
                "COUNT(l.place_id) AS like_count, " +
                "EXISTS (SELECT 1 FROM likes l2 WHERE l2.place_id = p.place_id AND l2.traveller_id = ?) AS liked " +
                "FROM places p " +
                "JOIN likes l ON p.place_id = l.place_id " +
                "GROUP BY p.place_id " +
                "HAVING COUNT(l.place_id) > 0 " +
                "ORDER BY like_count DESC " +
                "LIMIT 10";


        return jdbcTemplate.query(sql, new Object[]{travellerId}, (rs, rowNum) -> {
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
            return dto;
        });
    }

    //get favourite places
    public List<DashboardPlaceDTO> getFavouritePlaces(Long travellerId) {
        String sql = "SELECT p.*, " +
                "EXISTS (SELECT 1 FROM likes l2 WHERE l2.place_id = p.place_id AND l2.traveller_id = ?) AS liked " +
                "FROM places p " +
                "JOIN likes l ON p.place_id = l.place_id " +
                "WHERE l.traveller_id = ? " +
                "GROUP BY p.place_id " +
                "LIMIT 10";

        return jdbcTemplate.query(sql, new Object[]{travellerId, travellerId}, (rs, rowNum) -> {
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
            return dto;
        });
    }
}
