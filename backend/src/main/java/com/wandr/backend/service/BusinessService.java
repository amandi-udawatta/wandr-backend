package com.wandr.backend.service;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.UserDetailsDTO;
import com.wandr.backend.dto.UserLoginDTO;
import com.wandr.backend.dto.business.BusinessDTO;
import com.wandr.backend.dto.business.BusinessSignupDTO;
import com.wandr.backend.dto.business.UpdateProfileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BusinessService {

    ApiResponse<UserDetailsDTO> loginBusiness(UserLoginDTO request);
    ApiResponse<UserDetailsDTO> registerBusiness(BusinessSignupDTO request, MultipartFile shopImageFileName, Integer shopCategory);
    public ApiResponse<String> updateProfile(Long businessId, UpdateProfileDTO request);

    ApiResponse<Void> updateBusinessJwt (String jwt, Long businessId);

    public String getSalt(String email);

    ApiResponse<List<BusinessDTO>> getPendingBusinesses();


    }
