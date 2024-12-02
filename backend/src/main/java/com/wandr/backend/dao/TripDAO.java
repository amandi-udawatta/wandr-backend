package com.wandr.backend.dao;


import com.wandr.backend.dto.ApiResponse;
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
        String sql = "INSERT INTO trips (traveller_id, name, created_at, updated_at, status, ordered_time, optimized_time) VALUES (?, ?, ?, ?, ?, ?, ? ) RETURNING trip_id";
        return jdbcTemplate.queryForObject(sql, Long.class, trip.getTravellerId(), trip.getName(), trip.getCreatedAt(), trip.getUpdatedAt(), trip.getStatus(), trip.getOrderedTime(), trip.getOptimizedTime());
    }


    public Trip findById(Long tripId) {
        String sql = "SELECT * FROM trips WHERE trip_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{tripId}, new TripRowMapper());
    }

    public void update(Trip trip) {
        String sql = "UPDATE trips SET name = ?, updated_at = ?, status = ?, optimized_time = ?, ordered_time = ?, ordered_distance = ?, optimized_distance = ?, start_time = ?, end_time = ?, start_lat = ?, start_lng = ?, end_lat = ?, end_lng = ?, route_type = ? WHERE trip_id = ?";
        jdbcTemplate.update(sql, trip.getName(), trip.getUpdatedAt(), trip.getStatus(),trip.getOptimizedTime(), trip.getOrderedTime(), trip.getOrderedDistance(), trip.getOptimizedDistance(), trip.getStartTime(), trip.getEndTime(), trip.getStart_lat(), trip.getStart_lng(), trip.getEnd_lat(), trip.getEnd_lng(), trip.getRouteType(), trip.getTripId());
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

    //find traveller by trip id
    //findTravellerById
    public Long findTravellerById(Long tripId) {
        String sql = "SELECT traveller_id FROM trips WHERE trip_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, tripId);
    }



}
