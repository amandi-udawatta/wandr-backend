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
        trip.setShortestTime(rs.getInt("shortest_time"));
        trip.setPreferredTime(rs.getInt("preferred_time"));
        trip.setOrderedTime(rs.getInt("order_time"));
        return trip;
    }
}
