package com.wandr.backend.service;

import com.wandr.backend.dto.*;
import com.wandr.backend.dto.traveller.TravellerSignupDTO;
import com.wandr.backend.dto.traveller.UpdateActivitiesDTO;
import com.wandr.backend.dto.traveller.UpdateCategoriesDTO;
import com.wandr.backend.dto.traveller.UpdateProfileDTO;

public interface TravellerService {

    ApiResponse<UserDetailsDTO> loginTraveller(UserLoginDTO request);
    ApiResponse<UserDetailsDTO> registerTraveller(TravellerSignupDTO request);
    ApiResponse<String> updateCategories(Long travellerId, UpdateCategoriesDTO request);
    ApiResponse<String> updateActivities(Long travellerId, UpdateActivitiesDTO request);
    ApiResponse<String> updateProfile(Long travellerId, UpdateProfileDTO request);

    ApiResponse<Void> updateTravellerJwt (String jwt, Long travellerId);

    public String getSalt(String email);

}
