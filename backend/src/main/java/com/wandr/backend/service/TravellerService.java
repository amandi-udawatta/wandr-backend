package com.wandr.backend.service;

import com.wandr.backend.dto.*;
import com.wandr.backend.dto.chat.ChattedTravellerDTO;
import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.dto.recommendation.RecommendedPlaceDTO;
import com.wandr.backend.dto.traveller.*;

import java.util.List;

public interface TravellerService {

    ApiResponse<UserDetailsDTO> loginTraveller(UserLoginDTO request);
    ApiResponse<UserDetailsDTO> registerTraveller(TravellerSignupDTO request);
    ApiResponse<String> updateCategories(Long travellerId, UpdateCategoriesDTO request);
    ApiResponse<String> updateActivities(Long travellerId, UpdateActivitiesDTO request);
    ApiResponse<TravellerDTO> updateProfile(Long travellerId, UpdateProfileDTO request);

    ApiResponse<Void> updateTravellerJwt (String jwt, Long travellerId);

    public String getSalt(String email);

    //get popular places
    ApiResponse<List<DashboardPlaceDTO>> getPopularPlaces(Long travellerId);

    ApiResponse<List<DashboardPlaceDTO>> getFavouritePlaces(Long travellerId);

    ApiResponse<List<DashboardPlaceDTO>> getPlacesForTraveller(long travellerId);

    ApiResponse<Void> logout(Long travellerId);

    TravellerDTO getById(Long travellerId);

    List<RecommendedPlaceDTO> getRecommendedPlaces(Long travellerId);

    ApiResponse<List<DashboardPlaceDTO>> getRecommendedPlacesForDashboard(Long travellerId);

    ApiResponse<List<ChattedBusinessDTO>> getChattedBusinesses(Long travellerId);












}
