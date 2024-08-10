package com.wandr.backend.dao;

import com.wandr.backend.dto.trip.TripPlaceDTO;
import com.wandr.backend.entity.TripPlace;
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

    public List<TripPlaceDTO> getTripPlaces(Long tripId) {
        String sql = "SELECT tp.trip_place_id, tp.place_id, tp.title, tp.place_order, tp.rating, p.name AS place_name " +
                "FROM trip_places tp JOIN places p ON tp.place_id = p.place_id WHERE tp.trip_id = ? ORDER BY tp.place_order ASC";

        return jdbcTemplate.query(sql, new Object[]{tripId}, (rs, rowNum) -> {
            TripPlaceDTO tripPlaceDTO = new TripPlaceDTO();
            tripPlaceDTO.setTripPlaceId(rs.getLong("trip_place_id"));
            tripPlaceDTO.setPlaceId(rs.getLong("place_id"));
            tripPlaceDTO.setTitle(rs.getString("title"));
            tripPlaceDTO.setPlaceOrder(rs.getInt("place_order"));
            tripPlaceDTO.setRating(rs.getInt("rating"));
            return tripPlaceDTO;
        });
    }

    //rate trip place
    public void rateTripPlace(Long tripPlaceId, Integer rating) {
        String sql = "UPDATE trip_places SET rating = ? WHERE trip_place_id = ?";
        jdbcTemplate.update(sql, rating, tripPlaceId);
    }
}
