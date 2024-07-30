package com.wandr.backend.service.impl;

import com.wandr.backend.dao.PlaceDAO;
import com.wandr.backend.dao.TripDAO;
import com.wandr.backend.dao.TripPlaceDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.trip.AddPlaceToTripDTO;
import com.wandr.backend.dto.trip.CreateTripDTO;
import com.wandr.backend.entity.Places;
import com.wandr.backend.entity.Trip;
import com.wandr.backend.entity.TripPlace;
import com.wandr.backend.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class TripServiceImpl implements TripService {

    private final TripDAO tripDAO;
    private final TripPlaceDAO tripPlaceDAO;

    private final PlaceDAO placeDAO;

    public TripServiceImpl(TripDAO tripDAO, TripPlaceDAO tripPlaceDAO, PlaceDAO placeDAO) {
        this.tripDAO = tripDAO;
        this.tripPlaceDAO = tripPlaceDAO;
        this.placeDAO = placeDAO;
    }

    @Override
    @Transactional
    public ApiResponse<Void> createTrip(CreateTripDTO createTripDTO) {
        try {
            Trip trip = new Trip();
            trip.setTravellerId(createTripDTO.getTravellerId());
            trip.setName(createTripDTO.getName());
            trip.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            trip.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            trip.setStatus("pending");
            //initialize estimated time by default to 0
            trip.setShortestTime(0);
            trip.setPreferredTime(0);
            trip.setOrderedTime(0);
            //get id returned by createTrip
            Long id = tripDAO.createTrip(trip);

            Places placeName = placeDAO.findById(createTripDTO.getPlaceId());
            TripPlace tripPlace = new TripPlace();
            tripPlace.setTripId(id);
            tripPlace.setTitle(placeName.getName());
            tripPlace.setPlaceId(createTripDTO.getPlaceId());
            tripPlace.setVisited(false);
            tripPlace.setPlaceOrder(1);

            tripPlaceDAO.addTripPlace(tripPlace);

            return new ApiResponse<>(true, HttpStatus.CREATED.value(), "Trip created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating trip");
        }
    }


    @Override
    public ApiResponse<Void> addPlaceToTrip(AddPlaceToTripDTO addPlaceToTripDTO) {
        if(tripPlaceDAO.checkIfPlaceExists(addPlaceToTripDTO.getTripId(), addPlaceToTripDTO.getPlaceId())) {
            return new ApiResponse<>(false, 400, "Place already exists in the trip");
        }
        TripPlace tripPlace = new TripPlace();
        Places placeName = placeDAO.findById(addPlaceToTripDTO.getPlaceId());
        tripPlace.setTitle(placeName.getName());
        tripPlace.setTripId(addPlaceToTripDTO.getTripId());
        tripPlace.setPlaceId(addPlaceToTripDTO.getPlaceId());
        tripPlace.setPlaceOrder(tripPlaceDAO.getNextPlaceOrder(addPlaceToTripDTO.getTripId()));
        tripPlace.setVisited(false);
        //update updated time of trips entity
        Trip trip = tripDAO.findById(addPlaceToTripDTO.getTripId());
        trip.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        tripPlaceDAO.addTripPlace(tripPlace);
        tripDAO.update(trip);

        return new ApiResponse<>(true, 200, "Place added to trip successfully");
    }
}
