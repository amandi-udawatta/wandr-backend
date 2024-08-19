package com.wandr.backend.mapper;

import com.wandr.backend.entity.Trip;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class TripRowMapper implements RowMapper<Trip> {

    @Override
    public Trip mapRow(ResultSet rs, int rowNum) throws SQLException {
        Trip trip = new Trip();
        trip.setTripId(rs.getLong("trip_id"));
        trip.setTravellerId(rs.getInt("traveller_id"));
        trip.setName(rs.getString("name"));
        trip.setCreatedAt(rs.getTimestamp("created_at"));
        trip.setUpdatedAt(rs.getTimestamp("updated_at"));
        trip.setStatus(rs.getString("status"));
        trip.setRouteType(rs.getLong("route_type"));
        trip.setOptimizedTime(rs.getInt("optimized_time"));
        trip.setOrderedTime(rs.getInt("ordered_time"));
        trip.setOptimizedDistance(rs.getInt("optimized_distance"));
        trip.setOrderedDistance(rs.getInt("ordered_distance"));
        trip.setStartTime(rs.getTimestamp("start_time"));
        trip.setEndTime(rs.getTimestamp("end_time"));
        trip.setStart_lat(rs.getDouble("start_lat"));
        trip.setStart_lng(rs.getDouble("start_lng"));
        trip.setEnd_lat(rs.getDouble("end_lat"));
        trip.setEnd_lng(rs.getDouble("end_lng"));
        return trip;
    }
}
