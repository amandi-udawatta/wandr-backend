package com.wandr.backend.service;

import com.wandr.backend.dto.*;

public interface TravellerService {

    ApiResponse<String> loginTraveller(TravellerLoginDTO request);
    ApiResponse<String> registerTraveller(TravellerSignupDTO request);
    ApiResponse<String> updateCategories(Long travellerId, UpdateCategoriesDTO request);
    ApiResponse<String> updateActivities(Long travellerId, UpdateActivitiesDTO request);
    ApiResponse<String> updateProfile(Long travellerId, UpdateProfileDTO request);

}
