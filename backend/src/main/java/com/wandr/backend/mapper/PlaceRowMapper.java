package com.wandr.backend.mapper;

import com.wandr.backend.entity.Places;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceRowMapper implements RowMapper<Places> {

    @Override
    public Places mapRow(ResultSet rs, int rowNum) throws SQLException {
        Places place = new Places();
        place.setId(rs.getLong("place_id"));
        place.setName(rs.getString("name"));
        place.setDescription(rs.getString("description"));
        place.setLatitude(rs.getDouble("latitude"));
        place.setLongitude(rs.getDouble("longitude"));
        place.setAddress(rs.getString("address"));
        String image = rs.getString("image");
        String categories = rs.getString("categories");
        if (categories != null && !categories.trim().isEmpty()) {
            List<Long> categoryList = Arrays.stream(categories.replaceAll("[\\[\\]\\s]", "").split(","))
                    .filter(str -> !str.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            place.setCategories(categoryList);
        } else {
            place.setCategories(Collections.emptyList());
        }

        String activities = rs.getString("activities");
        if (activities != null && !activities.trim().isEmpty()) {
            List<Long> activityList = Arrays.stream(activities.replaceAll("[\\[\\]\\s]", "").split(","))
                    .filter(str -> !str.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            place.setActivities(activityList);
        } else {
            place.setActivities(Collections.emptyList());
        }
        return place;
    }
}
