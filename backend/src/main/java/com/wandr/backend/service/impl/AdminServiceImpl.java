package com.wandr.backend.service.impl;

import com.wandr.backend.dao.AdminDAO;
import com.wandr.backend.dto.*;
import com.wandr.backend.entity.Admin;
import com.wandr.backend.enums.Role;
import com.wandr.backend.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminDAO adminDAO;

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    public AdminServiceImpl(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }
    @Override
    public ApiResponse<Void> updateAdminJwt (String jwt, Long adminId) {
        adminDAO.updateAdminJwt(jwt, adminId);
        return new ApiResponse<>(true, 200, "JWT updated successfully");
    }

    @Override
    public String getSalt(String email){
        Optional<Admin> adminOpt = adminDAO.findByEmail(email);
        if (adminOpt.isEmpty()) {
            return null;
        }
        Admin admin = adminOpt.get();
        return admin.getSalt();
    }


    @Override
    public ApiResponse<UserDetailsDTO> loginAdmin(UserLoginDTO request) {
        Optional<Admin> adminOpt = adminDAO.findByEmail(request.getEmail());

        if (adminOpt.isEmpty() || !request.getPassword().equals(adminOpt.get().getPassword())) {
            logger.error("Invalid email or password for admin with email: {}", request.getEmail());
            return new ApiResponse<>(false, 401, "Invalid email or password");
        }


        Admin admin = adminOpt.get();
        UserDetailsDTO userDetails = new UserDetailsDTO(
                admin.getAdminId(),
                admin.getEmail(),
                Role.ADMIN,
                admin.getName()
        );

        logger.info("Admin with email: {} logged in successfully", request.getEmail());
        return new ApiResponse<>(true, 200, "Admin login successful", userDetails);
    }




//    @Override
//    public ApiResponse<String> updateProfile(Long travellerId, UpdateProfileDTO request) {
//        Traveller existingTraveller = travellerDAO.findById(travellerId);
//        if (existingTraveller == null) {
//            return new ApiResponse<>(false, 404, "Traveller not found");
//        }
//
//        if (request.getName() != null) {
//            existingTraveller.setName(request.getName());
//        }
//        if (request.getEmail() != null) {
//            existingTraveller.setEmail(request.getEmail());
//        }
//        if (request.getCountry() != null) {
//            existingTraveller.setCountry(request.getCountry());
//        }
//        if (request.getCategories() != null) {
//            existingTraveller.setCategories(request.getCategories());
//        }
//        if (request.getActivities() != null) {
//            existingTraveller.setActivities(request.getActivities());
//        }
//        if (request.getProfileImage() != null) {
//            existingTraveller.setProfileImage(request.getProfileImage());
//        }
//
//        travellerDAO.updateProfile(existingTraveller);
//
//        return new ApiResponse<>(true, 200, "Profile updated successfully");
//    }


}
