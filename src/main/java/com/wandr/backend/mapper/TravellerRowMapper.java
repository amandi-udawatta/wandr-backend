package com.wandr.backend.mapper;

import com.wandr.backend.entity.Traveller;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TravellerRowMapper implements RowMapper<Traveller> {

    @Override
    public Traveller mapRow(ResultSet rs, int rowNum) throws SQLException {
        Traveller traveller = new Traveller();
        traveller.setTravellerId(rs.getLong("traveller_id"));
        traveller.setName(rs.getString("name"));
        traveller.setEmail(rs.getString("email"));
        traveller.setPassword(rs.getString("password"));
        traveller.setCountry(rs.getString("country"));

        String categories = rs.getString("categories");
        if (categories != null) {
            List<Integer> categoryList = Arrays.stream(categories.replaceAll("[\\[\\]\\s]", "").split(","))
                    .map(Integer::parseInt).collect(Collectors.toList());
            traveller.setCategories(categoryList);
        }

        String activities = rs.getString("activities");
        if (activities != null) {
            List<Integer> activityList = Arrays.stream(activities.replaceAll("[\\[\\]\\s]", "").split(","))
                    .map(Integer::parseInt).collect(Collectors.toList());
            traveller.setActivities(activityList);
        }

        return traveller;
    }
}
