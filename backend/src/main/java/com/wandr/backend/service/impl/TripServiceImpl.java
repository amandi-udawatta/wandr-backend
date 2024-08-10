package com.wandr.backend.service.impl;

import com.wandr.backend.dao.PlaceDAO;
import com.wandr.backend.dao.TripDAO;
import com.wandr.backend.dao.TripPlaceDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.trip.AddPlaceToTripDTO;
import com.wandr.backend.dto.trip.CreateTripDTO;
import com.wandr.backend.dto.trip.PendingTripsDTO;
import com.wandr.backend.dto.trip.TripPlaceDTO;
import com.wandr.backend.entity.Places;
import com.wandr.backend.entity.Trip;
import com.wandr.backend.entity.TripPlace;
import com.wandr.backend.service.TripService;
import com.wandr.backend.util.GoogleMapsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TripServiceImpl implements TripService {

    private final TripDAO tripDAO;
    private final TripPlaceDAO tripPlaceDAO;

    private final GoogleMapsUtil googleMapsUtil;


    private final PlaceDAO placeDAO;

    public TripServiceImpl(TripDAO tripDAO, TripPlaceDAO tripPlaceDAO, PlaceDAO placeDAO, GoogleMapsUtil googleMapsUtil) {
        this.tripDAO = tripDAO;
        this.tripPlaceDAO = tripPlaceDAO;
        this.placeDAO = placeDAO;
        this.googleMapsUtil = googleMapsUtil;
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
            trip.setOrderTime(0);
            //get id returned by createTrip
            Long id = tripDAO.createTrip(trip);

            Places placeName = placeDAO.findById(createTripDTO.getPlaceId());
            TripPlace tripPlace = new TripPlace();
            tripPlace.setTripId(id);
            tripPlace.setTitle(placeName.getName());
            tripPlace.setPlaceId(createTripDTO.getPlaceId());
            tripPlace.setVisited(false);
            tripPlace.setPlaceOrder(1);
            tripPlace.setRating(0);

            tripPlaceDAO.addTripPlace(tripPlace);

            return new ApiResponse<>(true, HttpStatus.CREATED.value(), "Trip created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating trip");
        }
    }


    @Override
    public ApiResponse<Void> addPlaceToTrip(AddPlaceToTripDTO addPlaceToTripDTO) {
        if (tripPlaceDAO.checkIfPlaceExists(addPlaceToTripDTO.getTripId(), addPlaceToTripDTO.getPlaceId())) {
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


    //update trip order with route type
    @Override
    public ApiResponse<Void> updateTripOrder(Long tripId, String orderType) {
        try {
            List<Long> placeIds = tripPlaceDAO.getPlaceIdsByTripId(tripId);
            System.out.println("placeIds: " + placeIds);
            //check if ordertype is shortest
            //methana indn wada naa check!
            if (Objects.equals(orderType, "shortest")) {
                System.out.println("shortest");
                // Convert placeIds to coordinates
                List<String> coordinates = getCoordinatesFromPlaceIds(placeIds);
                System.out.println("coordinates: " + coordinates);
                List<String> orderedCoordinates = googleMapsUtil.getShortestRoute(coordinates);
                System.out.println("orderedCoordinates: " + orderedCoordinates);

                // Update trip place order based on orderedCoordinates
                updateTripPlaceOrder(tripId, orderedCoordinates);
            }
            return new ApiResponse<>(true, 200, "Trip order updated successfully");
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Error updating trip order: " + e.getMessage());
        }
    }

    private List<String> getCoordinatesFromPlaceIds(List<Long> placeIds) {
        return tripDAO.getCoordinatesFromPlaceIds(placeIds);
    }

    private void updateTripPlaceOrder(Long tripId, List<String> orderedCoordinates) {
        List<Long> orderedPlaceIds = new ArrayList<>();
        for (String orderedCoordinate : orderedCoordinates) {
            Long placeId = placeDAO.getPlaceIdFromCoordinate(orderedCoordinate);
            orderedPlaceIds.add(placeId);
        }
        tripDAO.updateTripPlaceOrder(tripId, orderedPlaceIds);
    }

    @Override
    public ApiResponse<List<PendingTripsDTO>> getPendingTrips(Long travellerId) {
        try {
            List<Trip> trips = tripDAO.getPendingTrips(travellerId);
            if (trips.isEmpty()) {
                return new ApiResponse<>(false, 404, "No pending trips found");
            }
            List<PendingTripsDTO> pendingTripsDTOList = new ArrayList<>();

            for (Trip trip : trips) {
                List<TripPlaceDTO> tripPlaces = tripPlaceDAO.getTripPlaces(trip.getTripId());
                PendingTripsDTO pendingTripDTO = new PendingTripsDTO();
                pendingTripDTO.setTripId(trip.getTripId());
                pendingTripDTO.setName(trip.getName());
                pendingTripDTO.setRouteType(trip.getRouteType());
                pendingTripDTO.setCreatedAt(trip.getCreatedAt());
                pendingTripDTO.setUpdatedAt(trip.getUpdatedAt());
                pendingTripDTO.setTripPlaces(tripPlaces);
                pendingTripDTO.setShortestTime(trip.getShortestTime());
                pendingTripDTO.setPreferredTime(trip.getPreferredTime());
                pendingTripDTO.setOrderTime(trip.getOrderTime());


                pendingTripsDTOList.add(pendingTripDTO);
            }


            return new ApiResponse<>(true, 200, "Pending trips retrieved successfully", pendingTripsDTOList);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "An error occurred while retrieving pending trips");
        }
    }

    @Override
    public ApiResponse<List<PendingTripsDTO>> getFinalizedTrips(Long travellerId) {
        try {
            List<Trip> trips = tripDAO.getFinalizedTrips(travellerId);
            if (trips.isEmpty()) {
                return new ApiResponse<>(false, 404, "No finalized trips found");
            }
            List<PendingTripsDTO> finalizedTripsDTOList = new ArrayList<>();

            for (Trip trip : trips) {
                List<TripPlaceDTO> tripPlaces = tripPlaceDAO.getTripPlaces(trip.getTripId());
                PendingTripsDTO finalizedTripDTO = new PendingTripsDTO();
                finalizedTripDTO.setTripId(trip.getTripId());
                finalizedTripDTO.setName(trip.getName());
                finalizedTripDTO.setRouteType(trip.getRouteType());
                finalizedTripDTO.setCreatedAt(trip.getCreatedAt());
                finalizedTripDTO.setUpdatedAt(trip.getUpdatedAt());
                finalizedTripDTO.setTripPlaces(tripPlaces);
                finalizedTripDTO.setShortestTime(trip.getShortestTime());
                finalizedTripDTO.setPreferredTime(trip.getPreferredTime());
                finalizedTripDTO.setOrderTime(trip.getOrderTime());


                finalizedTripsDTOList.add(finalizedTripDTO);
            }


            return new ApiResponse<>(true, 200, "Finalized trips retrieved successfully", finalizedTripsDTOList);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "An error occurred while retrieving finalized trips");
        }
    }

    //get ongoing trip
    @Override
    public ApiResponse<PendingTripsDTO> getOngoingTrip(Long travellerId) {
        try {
            Trip trip = tripDAO.getOngoingTrip(travellerId);
            if (trip == null) {
                return new ApiResponse<>(false, 404, "No ongoing trip found");
            }
            List<TripPlaceDTO> tripPlaces = tripPlaceDAO.getTripPlaces(trip.getTripId());
            PendingTripsDTO ongoingTripDTO = new PendingTripsDTO();
            ongoingTripDTO.setTripId(trip.getTripId());
            ongoingTripDTO.setName(trip.getName());
            ongoingTripDTO.setRouteType(trip.getRouteType());
            ongoingTripDTO.setCreatedAt(trip.getCreatedAt());
            ongoingTripDTO.setUpdatedAt(trip.getUpdatedAt());
            ongoingTripDTO.setTripPlaces(tripPlaces);
            ongoingTripDTO.setShortestTime(trip.getShortestTime());
            ongoingTripDTO.setPreferredTime(trip.getPreferredTime());
            ongoingTripDTO.setOrderTime(trip.getOrderTime());

            return new ApiResponse<>(true, 200, "Ongoing trip retrieved successfully", ongoingTripDTO);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "An error occurred while retrieving ongoing trip");
        }
    }

    //Rating stars for a trip place
    @Override
    public ApiResponse<Void> ratePlace(Long tripPlaceId, int rating) {
        try {
            tripPlaceDAO.rateTripPlace(tripPlaceId, rating);
            return new ApiResponse<>(true, 200, "Trip place rated successfully");
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "An error occurred while rating trip place");
        }
    }
}
