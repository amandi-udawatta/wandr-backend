package com.wandr.backend.dao;

import com.wandr.backend.entity.Trip;
import com.wandr.backend.mapper.TripRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TripDAO {

    private final JdbcTemplate jdbcTemplate;

    public TripDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long createTrip(Trip trip) {
        String sql = "INSERT INTO trips (traveller_id, name, created_at, updated_at, status, estimated_time) VALUES (?, ?, ?, ?, ?, ? ) RETURNING trip_id";
        return jdbcTemplate.queryForObject(sql, Long.class, trip.getTravellerId(), trip.getName(), trip.getCreatedAt(), trip.getUpdatedAt(), trip.getStatus(), trip.getEstimatedTime());
    }


    public Trip findById(Long tripId) {
        String sql = "SELECT * FROM trips WHERE trip_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{tripId}, new TripRowMapper());
    }

    public void update(Trip trip) {
        String sql = "UPDATE trips SET name = ?, updated_at = ?, status = ? WHERE trip_id = ?";
        jdbcTemplate.update(sql, trip.getName(), trip.getUpdatedAt(), trip.getStatus(), trip.getTripId());
    }
}
