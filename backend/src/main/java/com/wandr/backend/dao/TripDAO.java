package com.wandr.backend.dao;


import com.wandr.backend.entity.Trip;
import com.wandr.backend.mapper.TripRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TripDAO {

    private final JdbcTemplate jdbcTemplate;

    public TripDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long createTrip(Trip trip) {
        String sql = "INSERT INTO trips (traveller_id, name, created_at, updated_at, status, shortest_time, preferred_time,order_time) VALUES (?, ?, ?, ?, ?, ?, ?, ? ) RETURNING trip_id";
        return jdbcTemplate.queryForObject(sql, Long.class, trip.getTravellerId(), trip.getName(), trip.getCreatedAt(), trip.getUpdatedAt(), trip.getStatus(), trip.getShortestTime(), trip.getPreferredTime(), trip.getOrderTime());
    }


    public Trip findById(Long tripId) {
        String sql = "SELECT * FROM trips WHERE trip_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{tripId}, new TripRowMapper());
    }

    public void update(Trip trip) {
        String sql = "UPDATE trips SET name = ?, updated_at = ?, status = ? WHERE trip_id = ?";
        jdbcTemplate.update(sql, trip.getName(), trip.getUpdatedAt(), trip.getStatus(), trip.getTripId());
    }

    // Method to get coordinates from place IDs (as shown above)
    public List<String> getCoordinatesFromPlaceIds(List<Long> placeIds) {
        String sql = "SELECT latitude, longitude FROM places WHERE place_id IN (" + placeIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getDouble("latitude") + "," + rs.getDouble("longitude"));
    }

    // Method to update trip place order
    public void updateTripPlaceOrder(Long tripId, List<Long> orderedPlaceIds) {
        for (int i = 0; i < orderedPlaceIds.size(); i++) {
            String sql = "UPDATE trip_places SET place_order = ? WHERE trip_id = ? AND place_id = ?";
            jdbcTemplate.update(sql, i + 1, tripId, orderedPlaceIds.get(i));
        }
    }

    public List<Trip> getPendingTrips(Long travellerId) {
        String sql = "SELECT * FROM trips WHERE traveller_id = ? AND status = 'pending'";
        return jdbcTemplate.query(sql, new Object[]{travellerId}, new TripRowMapper());
    }

    public List<Trip> getFinalizedTrips(Long travellerId) {
        String sql = "SELECT * FROM trips WHERE traveller_id = ? AND status = 'finalized'";
        return jdbcTemplate.query(sql, new Object[]{travellerId}, new TripRowMapper());
    }

    public Trip getOngoingTrip(Long travellerId) {
        String sql = "SELECT * FROM trips WHERE traveller_id = ? AND status = 'ongoing'";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{travellerId}, new TripRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
