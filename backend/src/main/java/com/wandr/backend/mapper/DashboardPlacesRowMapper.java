package com.wandr.backend.mapper;

import com.wandr.backend.dao.ActivityDAO;
import com.wandr.backend.dao.CategoryDAO;
import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.entity.Activity;
import com.wandr.backend.entity.Category;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardPlacesRowMapper implements RowMapper<DashboardPlaceDTO> {

    private final CategoryDAO categoryDAO;
    private final ActivityDAO activityDAO;

    public DashboardPlacesRowMapper(CategoryDAO categoryDAO, ActivityDAO activityDAO) {
        this.categoryDAO = categoryDAO;
        this.activityDAO = activityDAO;
    }

    @Override
    public DashboardPlaceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
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
}
