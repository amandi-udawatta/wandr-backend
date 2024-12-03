package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.UserDetailsDTO;
import com.wandr.backend.dto.UserLoginDTO;
import com.wandr.backend.dto.business.*;
import com.wandr.backend.dto.chat.ChattedTravellerDTO;
import com.wandr.backend.dto.traveller.TravellerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BusinessService {

    ApiResponse<UserDetailsDTO> loginBusiness(UserLoginDTO request);
    ApiResponse<UserDetailsDTO> registerBusiness(BusinessSignupDTO request);
    ApiResponse<BusinessDTO> getBusinessById(Long businessId);
    ApiResponse<String> updateProfile(UpdateProfileDTO request);
    ApiResponse<Void> updateBusinessJwt (String jwt, Long businessId);
    ApiResponse<String> getSalt(String email);
    ApiResponse<List<BusinessDTO>> getPendingBusinesses();
    ApiResponse<List<BusinessDTO>> getApprovedBusinesses();

    ApiResponse<List<PaidBusinessDTO>> getPaidBusinesses();

    ApiResponse<List<PopularStoreDTO>> getPopularStores();
    ApiResponse<Void> approveBusiness(Long businessId);
    ApiResponse<Void> declineBusiness(Long businessId);

    ApiResponse<Void> logout(Long businessId);

    ApiResponse<Void> rateBusiness(Long travellerId, Long businessId, Integer rating);

    //get all chatted travellers
    ApiResponse<List<ChattedTravellerDTO>> getChattedTravellers(Long businessId);




}
