package com.wandr.backend.mapper;

import com.wandr.backend.entity.Places;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaceRowMapper implements RowMapper<Places> {

    @Override
    public Places mapRow(ResultSet rs, int rowNum) throws SQLException {
        Places place = new Places();
        place.setId(rs.getLong("id"));
        place.setName(rs.getString("name"));
        place.setDescription(rs.getString("description"));
        place.setLatitude(rs.getDouble("latitude"));
        place.setLongitude(rs.getDouble("longitude"));
        place.setAddress(rs.getString("address"));
        return place;
    }
}
