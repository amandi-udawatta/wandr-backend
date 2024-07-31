package com.wandr.backend.dao;

import com.wandr.backend.entity.TripPlace;
import com.wandr.backend.mapper.TripPlaceRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TripPlaceDAO {

    private final JdbcTemplate jdbcTemplate;

    public TripPlaceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addTripPlace(TripPlace tripPlace) {
        String sql = "INSERT INTO trip_places (trip_id, place_id, title, description, place_order, visited, image_name) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, tripPlace.getTripId(), tripPlace.getPlaceId(), tripPlace.getTitle(), tripPlace.getDescription(), tripPlace.getPlaceOrder(), tripPlace.getVisited(), tripPlace.getImageName());
    }

    public Integer getNextPlaceOrder(Long tripId) {
        String sql = "SELECT COALESCE(MAX(place_order), 0) + 1 FROM trip_places WHERE trip_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{tripId}, Integer.class);
    }

    //check if the place already exists in the given trip id
    public boolean checkIfPlaceExists(Long tripId, Long placeId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM trip_places WHERE trip_id = ? AND place_id = ?)";
        return jdbcTemplate.queryForObject(sql, new Object[]{tripId, placeId}, Boolean.class);
    }

    //get a long list of place ids by trip id
    public List<Long> getPlaceIdsByTripId(Long tripId) {
        String sql = "SELECT place_id FROM trip_places WHERE trip_id = ?";
        return jdbcTemplate.queryForList(sql, new Object[]{tripId}, Long.class);
    }
}
