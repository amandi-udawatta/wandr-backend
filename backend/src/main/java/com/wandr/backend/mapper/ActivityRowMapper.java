package com.wandr.backend.mapper;

import com.wandr.backend.entity.Activity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityRowMapper implements RowMapper<Activity> {

    @Override
    public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Activity activity = new Activity();
        activity.setId(rs.getLong("activity_id"));
        activity.setName(rs.getString("name"));
        return activity;
    }
}
