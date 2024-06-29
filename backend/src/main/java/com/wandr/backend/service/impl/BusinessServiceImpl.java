package com.wandr.backend.service.impl;

import com.wandr.backend.dao.BusinessDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.UserDetailsDTO;
import com.wandr.backend.dto.UserLoginDTO;
import com.wandr.backend.dto.business.BusinessSignupDTO;
import com.wandr.backend.dto.business.UpdateProfileDTO;
import com.wandr.backend.entity.Business;
import com.wandr.backend.enums.Role;
import com.wandr.backend.service.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BusinessServiceImpl implements BusinessService {

    private final BusinessDAO businessDAO;

    private static final Logger logger = LoggerFactory.getLogger(BusinessServiceImpl.class);

    @Autowired
    public BusinessServiceImpl(BusinessDAO businessDAO) {
        this.businessDAO = businessDAO;
    }
    @Override
    public ApiResponse<Void> updateBusinessJwt (String jwt, Long businessId) {
        businessDAO.updateBusinessJwt(jwt, businessId);
        return new ApiResponse<>(true, 200, "JWT updated successfully");
    }

    @Override
    public String getSalt(String email){
        Optional<Business> businessOpt = businessDAO.findByEmail(email);
        if (businessOpt.isEmpty()) {
            return null;
        }
        Business business = businessOpt.get();
        return business.getSalt();
    }


    @Override
    public ApiResponse<UserDetailsDTO> loginBusiness(UserLoginDTO request) {
        Optional<Business> businessOpt = businessDAO.findByEmail(request.getEmail());

        if (businessOpt.isEmpty() || !request.getPassword().equals(businessOpt.get().getPassword())) {
            logger.error("Invalid email or password for business with email: {}", request.getEmail());
            return new ApiResponse<>(false, 401, "Invalid email or password");
        }


        Business business = businessOpt.get();
        UserDetailsDTO userDetails = new UserDetailsDTO(
                business.getBusinessId(),
                business.getEmail(),
                Role.ADMIN,
                business.getName()
        );

        logger.info("Business with email: {} logged in successfully", request.getEmail());
        return new ApiResponse<>(true, 200, "Business login successful", userDetails);
    }




    @Override
    public ApiResponse<UserDetailsDTO> registerBusiness(BusinessSignupDTO request) {
        if (businessDAO.existsByEmail(request.getEmail())) {
            return new ApiResponse<>(false, 400, "Email already in use");
        }

        Business business = new Business();
        business.setName(request.getName());
        business.setDescription(request.getDescription());
        business.setServices(request.getServices());
        business.setAddress(request.getAddress());
        business.setLanguages(request.getLanguages());
        business.setWebsiteUrl(request.getWebsiteUrl());
        business.setBusinessContact(request.getBusinessContact());
        business.setShopImage(request.getShopImage());
        business.setCategoryId(request.getCategoryId());
        business.setOwnerName(request.getOwnerName());
        business.setOwnerContact(request.getOwnerContact());
        business.setOwnerNic(request.getOwnerNic());
        business.setEmail(request.getEmail());
        business.setPassword(request.getPassword());
        business.setApproved(false); // Default value, businesses need business approval
        business.setSalt(request.getSalt());
        business.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        businessDAO.save(business);

        return new ApiResponse<>(true, 201, "Business registered successfully");
    }

    @Override
    public ApiResponse<String> updateProfile(Long businessId, UpdateProfileDTO request) {
        Business existingBusiness = businessDAO.findById(businessId);
        if (existingBusiness == null) {
            return new ApiResponse<>(false, 404, "Business not found");
        }

        if (request.getName() != null) {
            existingBusiness.setName(request.getName());
        }
        if (request.getEmail() != null) {
            existingBusiness.setEmail(request.getEmail());
        }
        if (request.getDescription() != null) {
            existingBusiness.setDescription(request.getDescription());
        }
        if (request.getServices() != null) {
            existingBusiness.setServices(request.getServices());
        }
        if (request.getAddress() != null) {
            existingBusiness.setAddress(request.getAddress());
        }
        if (request.getLanguages() != null) {
            existingBusiness.setLanguages(request.getLanguages());
        }
        if (request.getWebsiteUrl() != null) {
            existingBusiness.setWebsiteUrl(request.getWebsiteUrl());
        }
        if (request.getBusinessContact() != null) {
            existingBusiness.setBusinessContact(request.getBusinessContact());
        }
        if (request.getShopImage() != null) {
            existingBusiness.setShopImage(request.getShopImage());
        }
        if (request.getCategoryId() != null) {
            existingBusiness.setCategoryId(request.getCategoryId());
        }
        if (request.getOwnerName() != null) {
            existingBusiness.setOwnerName(request.getOwnerName());
        }
        if (request.getOwnerContact() != null) {
            existingBusiness.setOwnerContact(request.getOwnerContact());
        }
        if (request.getOwnerNic() != null) {
            existingBusiness.setOwnerNic(request.getOwnerNic());
        }

        businessDAO.updateProfile(existingBusiness);

        return new ApiResponse<>(true, 200, "Profile updated successfully");
    }


}
