package com.wandr.backend.mapper;

import com.wandr.backend.entity.TripPlace;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TripPlaceRowMapper implements RowMapper<TripPlace> {

    @Override
    public TripPlace mapRow(ResultSet rs, int rowNum) throws SQLException {
        TripPlace tripPlace = new TripPlace();
        tripPlace.setTripPlaceId(rs.getLong("trip_place_id"));
        tripPlace.setTripId(rs.getLong("trip_id"));
        tripPlace.setPlaceId(rs.getLong("place_id"));
        tripPlace.setTitle(rs.getString("title"));
        tripPlace.setDescription(rs.getString("description"));
        tripPlace.setPlaceOrder(rs.getInt("place_order"));
        tripPlace.setOptimizedOrder(rs.getInt("optimized_order"));
        tripPlace.setVisited(rs.getBoolean("visited"));
        tripPlace.setImageName(rs.getString("image_name"));
        tripPlace.setRating(rs.getInt("rating"));
        return tripPlace;
    }
}
