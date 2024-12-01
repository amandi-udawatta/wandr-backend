package com.wandr.backend.service.impl;

import com.wandr.backend.dao.PlaceDAO;
import com.wandr.backend.dao.TravellerDAO;
import com.wandr.backend.dao.TripDAO;
import com.wandr.backend.dao.TripPlaceDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.dto.trip.*;
import com.wandr.backend.entity.Places;
import com.wandr.backend.entity.Trip;
import com.wandr.backend.entity.TripPlace;
import com.wandr.backend.service.TripService;
import com.wandr.backend.util.GoogleMapsDistanceMatrixUtil;
import com.wandr.backend.util.RouteOptimizationUtil;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements TripService {

    private final TripDAO tripDAO;
    private final TripPlaceDAO tripPlaceDAO;

    private final RouteOptimizationUtil routeOptimizationUtil;

    private final GoogleMapsDistanceMatrixUtil googleMapsDistanceMatrixUtil;

    private final PlaceDAO placeDAO;

    private final TravellerDAO travellerDAO;

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);
    public TripServiceImpl(TripDAO tripDAO, TripPlaceDAO tripPlaceDAO, PlaceDAO placeDAO, TravellerDAO travellerDAO, RouteOptimizationUtil routeOptimizationUtil, GoogleMapsDistanceMatrixUtil googleMapsDistanceMatrixUtil) {
        this.tripDAO = tripDAO;
        this.tripPlaceDAO = tripPlaceDAO;
        this.travellerDAO = travellerDAO;
        this.placeDAO = placeDAO;
        this.routeOptimizationUtil = routeOptimizationUtil;
        this.googleMapsDistanceMatrixUtil = googleMapsDistanceMatrixUtil;
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
            trip.setOrderedTime(0);
            trip.setOptimizedTime(0);
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

                // Calculate estimated times
                int estimatedOrderedTime = calculateEstimatedTime(trip.getOrderedTime(), tripPlaces.size());
                int estimatedOptimizedTime = calculateEstimatedTime(trip.getOptimizedTime(), tripPlaces.size());

                PendingTripsDTO pendingTripDTO = new PendingTripsDTO();
                pendingTripDTO.setTripId(trip.getTripId());
                pendingTripDTO.setName(trip.getName());
                pendingTripDTO.setRouteType(trip.getRouteType());
                pendingTripDTO.setCreatedAt(trip.getCreatedAt());
                pendingTripDTO.setUpdatedAt(trip.getUpdatedAt());
                pendingTripDTO.setTripPlaces(tripPlaces);
                pendingTripDTO.setOrderedTime(trip.getOrderedTime());
                pendingTripDTO.setOptimizedTime(trip.getOptimizedTime());
                pendingTripDTO.setOrderedDistance(trip.getOrderedDistance());
                pendingTripDTO.setOptimizedDistance(trip.getOptimizedDistance());
                pendingTripDTO.setEstimatedOrderedTime(estimatedOrderedTime);
                pendingTripDTO.setEstimatedOptimizedTime(estimatedOptimizedTime);
                pendingTripDTO.setStart_lat(trip.getStart_lat());
                pendingTripDTO.setStart_lng(trip.getStart_lng());
                pendingTripDTO.setEnd_lat(trip.getEnd_lat());
                pendingTripDTO.setEnd_lng(trip.getEnd_lng());

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

                // Calculate estimated times
                int estimatedOrderedTime = calculateEstimatedTime(trip.getOrderedTime(), tripPlaces.size());
                int estimatedOptimizedTime = calculateEstimatedTime(trip.getOptimizedTime(), tripPlaces.size());

                PendingTripsDTO finalizedTripDTO = new PendingTripsDTO();
                finalizedTripDTO.setTripId(trip.getTripId());
                finalizedTripDTO.setName(trip.getName());
                finalizedTripDTO.setRouteType(trip.getRouteType());
                finalizedTripDTO.setCreatedAt(trip.getCreatedAt());
                finalizedTripDTO.setUpdatedAt(trip.getUpdatedAt());
                finalizedTripDTO.setTripPlaces(tripPlaces);
                finalizedTripDTO.setOrderedTime(trip.getOrderedTime());
                finalizedTripDTO.setOptimizedTime(trip.getOptimizedTime());
                finalizedTripDTO.setOrderedDistance(trip.getOrderedDistance());
                finalizedTripDTO.setOptimizedDistance(trip.getOptimizedDistance());
                finalizedTripDTO.setEstimatedOrderedTime(estimatedOrderedTime);
                finalizedTripDTO.setEstimatedOptimizedTime(estimatedOptimizedTime);
                finalizedTripDTO.setStart_lat(trip.getStart_lat());
                finalizedTripDTO.setStart_lng(trip.getStart_lng());
                finalizedTripDTO.setEnd_lat(trip.getEnd_lat());
                finalizedTripDTO.setEnd_lng(trip.getEnd_lng());

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

            // Calculate estimated times
            int estimatedOrderedTime = calculateEstimatedTime(trip.getOrderedTime(), tripPlaces.size());
            int estimatedOptimizedTime = calculateEstimatedTime(trip.getOptimizedTime(), tripPlaces.size());

            PendingTripsDTO ongoingTripDTO = new PendingTripsDTO();
            ongoingTripDTO.setTripId(trip.getTripId());
            ongoingTripDTO.setName(trip.getName());
            ongoingTripDTO.setRouteType(trip.getRouteType());
            ongoingTripDTO.setCreatedAt(trip.getCreatedAt());
            ongoingTripDTO.setUpdatedAt(trip.getUpdatedAt());
            ongoingTripDTO.setTripPlaces(tripPlaces);
            ongoingTripDTO.setOrderedTime(trip.getOrderedTime());
            ongoingTripDTO.setOptimizedTime(trip.getOptimizedTime());
            ongoingTripDTO.setOrderedDistance(trip.getOrderedDistance());
            ongoingTripDTO.setOptimizedDistance(trip.getOptimizedDistance());
            ongoingTripDTO.setEstimatedOrderedTime(estimatedOrderedTime);
            ongoingTripDTO.setEstimatedOptimizedTime(estimatedOptimizedTime);
            ongoingTripDTO.setStart_lat(trip.getStart_lat());
            ongoingTripDTO.setStart_lng(trip.getStart_lng());
            ongoingTripDTO.setEnd_lat(trip.getEnd_lat());
            ongoingTripDTO.setEnd_lng(trip.getEnd_lng());


            return new ApiResponse<>(true, 200, "Ongoing trip retrieved successfully", ongoingTripDTO);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "An error occurred while retrieving ongoing trip");
        }
    }

    //Rating stars for a trip place
    @Override
    public ApiResponse<Void> ratePlace(Long travellerId, Long placeId, Integer rating) {
        try {
            travellerDAO.upsertPlaceRating(travellerId, placeId, rating);
            travellerDAO.updateAverageRating(placeId);
            System.out.println("Rating updated successfully");
            return new ApiResponse<>(true, 200, "Trip place rated successfully");
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "An error occurred while rating trip place");
        }
    }


    //reorder places
    @Override
    public ApiResponse<TripTimeDTO> reorderTrip(Long tripId, List<PlaceOrderDTO> placeOrderList,double startLat, double startLng, double endLat, double endLng ) {
        try {
            for (PlaceOrderDTO placeOrder : placeOrderList) {
                TripPlace tripPlace = tripPlaceDAO.findByTripPlaceId(placeOrder.getTripPlaceId());
                if (Objects.isNull(tripPlace)) {
                    //return trip place with this trip place id not found
                    return new ApiResponse<>(false, 404, "Trip place with ID " + placeOrder.getTripPlaceId() + " not found");
                }
                tripPlace.setPlaceOrder(placeOrder.getOrder());
                tripPlaceDAO.updateTripPlaceOrder(tripPlace);
            }

            String routeOrder = "place_order";
            TripDurationAndDistanceDTO result = calculateTimeAndDistance(tripId, startLat, startLng, endLat, endLng, routeOrder );
            // Update trip's updated_at timestamp to reflect the change
            Trip trip = tripDAO.findById(tripId);
            trip.setOrderedDistance(result.getTotalDistance());
            trip.setOrderedTime(result.getTotalDuration());
            trip.setStart_lat(startLat);
            trip.setStart_lng(startLng);
            trip.setEnd_lat(endLat);
            trip.setEnd_lng(endLng);
            trip.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            tripDAO.update(trip);

            TripTimeDTO tripTimeDTO = new TripTimeDTO();
            tripTimeDTO.setTripId(tripId);
            tripTimeDTO.setOrderedTime(result.getTotalDuration());
            tripTimeDTO.setOrderedDistance(result.getTotalDistance());
            tripTimeDTO.setOptimizedTime(trip.getOptimizedTime());
            tripTimeDTO.setOptimizedDistance(trip.getOptimizedDistance());
            tripTimeDTO.setEstimatedOrderedTime(calculateEstimatedTime(result.getTotalDuration(), placeOrderList.size()));
            tripTimeDTO.setEstimatedOptimizedTime(calculateEstimatedTime(trip.getOptimizedTime(), placeOrderList.size()));

            return new ApiResponse<>(true, 200, "Trip reordered successfully", tripTimeDTO);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "An error occurred while reordering trip places");
        }
    }

    private TripDurationAndDistanceDTO calculateTimeAndDistance(Long tripId, double startLat, double startLng, double endLat, double endLng, String routeOrder) {
        String origin = startLat + "," + startLng;
        String destination = endLat + "," + endLng;
        List<TripPlace> tripPlaces = tripPlaceDAO.getTripPlacesByTripIdForRoute(tripId, routeOrder);

        // Prepare the waypoints for Google Maps API (intermediates)
        List<String> intermediates = tripPlaces.stream()
                .map(this::formatLatLong)
                .collect(Collectors.toList());

        // Calculate duration and distance
        TripDurationAndDistanceDTO result = googleMapsDistanceMatrixUtil.calculateTripDurationAndDistance(origin, destination, intermediates);

        return result;

    }



    @Transactional
    @Override
    public ApiResponse<TripTimeDTO> optimizeTrip(Long tripId, double startLat, double startLng, double endLat, double endLng) {
        String routeOrder = "optimized_order";
        List<TripPlace> tripPlaces = tripPlaceDAO.getTripPlacesByTripIdForRoute(tripId, routeOrder);

        String origin = formatLatLong(startLat, startLng);
        String destination = formatLatLong(endLat, endLng);

        // Prepare the waypoints for Google Maps API (intermediates)
        List<String> intermediates = tripPlaces.stream()
                .map(this::formatLatLong)
                .collect(Collectors.toList());

        List<Integer> optimizedOrder = routeOptimizationUtil.optimizeRoute(origin, destination, intermediates);

        System.out.println("Optimized order: " + optimizedOrder);

        // Update the trip places in the database based on the optimized order
        for (int i = 0; i < optimizedOrder.size(); i++) {
            TripPlace tripPlace = tripPlaces.get(optimizedOrder.get(i));
            tripPlace.setOptimizedOrder(i + 1);
            tripPlaceDAO.updateOptimizedOrder(tripPlace);
        }

        TripDurationAndDistanceDTO result = calculateTimeAndDistance(tripId, startLat, startLng, endLat, endLng, routeOrder );
        // Update trip's updated_at timestamp to reflect the change
        Trip trip = tripDAO.findById(tripId);
        trip.setOptimizedDistance(result.getTotalDistance());
        trip.setOptimizedTime(result.getTotalDuration());
        trip.setStart_lat(startLat);
        trip.setStart_lng(startLng);
        trip.setEnd_lat(endLat);
        trip.setEnd_lng(endLng);
        trip.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        tripDAO.update(trip);

        TripTimeDTO tripTimeDTO = new TripTimeDTO();
        tripTimeDTO.setTripId(tripId);
        tripTimeDTO.setOrderedTime(trip.getOrderedTime());
        tripTimeDTO.setOrderedDistance(trip.getOrderedDistance());
        tripTimeDTO.setOptimizedTime(result.getTotalDuration());
        tripTimeDTO.setOptimizedDistance(result.getTotalDistance());
        tripTimeDTO.setEstimatedOrderedTime(calculateEstimatedTime(trip.getOrderedTime(), tripPlaces.size()));
        tripTimeDTO.setEstimatedOptimizedTime(calculateEstimatedTime(result.getTotalDuration(), tripPlaces.size()));


        return new ApiResponse<>(true, 200, "Trip optimized successfully", tripTimeDTO);
    }

    private String formatLatLong(double latitude, double longitude) {
        return latitude + "," + longitude;
    }

    private String formatLatLong(TripPlace tripPlace) {
        String latLong = placeDAO.getLatLongByPlaceId(tripPlace.getPlaceId());
        if (latLong == null || latLong.isEmpty()) {
            throw new RuntimeException("Latitude and longitude not found for place ID: " + tripPlace.getPlaceId());
        }
        return latLong;
    }


    @Override
    public ApiResponse<List<DashboardPlaceDTO>> getRecommendedPlacesForTrip(Long tripId) {
        // Fetch trip details and places
        Trip trip = tripDAO.findById(tripId);
        if (trip == null) {
            logger.error("Trip not found");
            return new ApiResponse<>(false, 404, "Trip not found", null);
        }
        List<TripPlace> tripPlaces = tripPlaceDAO.getTripPlacesByTripIdForRoute(tripId, "place_order");

        if (tripPlaces.isEmpty()) {
            logger.error("No places in the trip to calculate radius");
            return new ApiResponse<>(false, 400, "No places in the trip to calculate radius", null);
        }

        // Step 1: Prepare origins (trip places)
        List<String> tripPlaceCoordinates = tripPlaces.stream()
                .map(tp -> placeDAO.getLatLongByPlaceId(tp.getPlaceId())) // Fetch lat/lng
                .collect(Collectors.toList());

        // Step 2: Fetch recommended places for the traveler
        Long travellerId = tripDAO.findTravellerById(tripId);
        List<Long> recommendedPlaceIds = travellerDAO.getRecommendedPlaceIds(travellerId);

        //if there are recommendedPlaceIds which are in the tripPlaces list, remove them from the recommendedPlaceIds list
        recommendedPlaceIds.removeIf(tripPlace -> tripPlaces.stream().anyMatch(tp -> tp.getPlaceId().equals(tripPlace)));

        if (recommendedPlaceIds.isEmpty()) {
            logger.error("No recommended places found for this traveller");
            return new ApiResponse<>(false, 400, "No recommended places found for this traveller", null);
        }

        // Step 3: Prepare destinations (recommended places)
        List<String> recommendedPlaceCoordinates = recommendedPlaceIds.stream()
                .map(placeDAO::getLatLongByPlaceId)
                .collect(Collectors.toList());

        // Step 4: Use Distance Matrix API to filter places within 5km
        List<Long> filteredPlaceIds = googleMapsDistanceMatrixUtil.filterPlacesWithinRadius(
                tripPlaceCoordinates, recommendedPlaceIds, recommendedPlaceCoordinates, 5000);

        if (filteredPlaceIds.isEmpty()) {
            logger.info("No recommended places within the radius");
            return new ApiResponse<>(true, 200, "No recommended places within the radius", List.of());
        }

        // Step 5: Fetch details for filtered places
        List<DashboardPlaceDTO> filteredPlaces = placeDAO.getDashboardPlacesByIds(filteredPlaceIds, travellerId);

        // Step 6: Return filtered places as a response
        return new ApiResponse<>(true, 200, "Recommended places within the trip radius retrieved successfully", filteredPlaces);
    }

    //create estimated time for the trip with 6 hours to eat, 2 hours for meals, 2 hours for other activities, per day and 2 hours per trip place in the trip
    private int calculateEstimatedTime(int travelTimeMinutes, int numberOfPlaces) {
        // Static time per day (in minutes)
        int dailyStaticTimeMinutes = (6 + 2 + 2) * 60; // 6 hours eating + 2 hours meals + 2 hours activities
        // Time for each trip place (in minutes)
        int tripPlaceTimeMinutes = numberOfPlaces * 120; // 2 hours per place

        // Total estimated time
        return travelTimeMinutes + dailyStaticTimeMinutes + tripPlaceTimeMinutes;
    }




}
