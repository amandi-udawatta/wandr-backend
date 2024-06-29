package com.wandr.backend.service.impl;

import com.wandr.backend.dao.TravellerDAO;
import com.wandr.backend.dto.*;
import com.wandr.backend.dto.traveller.TravellerSignupDTO;
import com.wandr.backend.dto.traveller.UpdateActivitiesDTO;
import com.wandr.backend.dto.traveller.UpdateCategoriesDTO;
import com.wandr.backend.dto.traveller.UpdateProfileDTO;
import com.wandr.backend.entity.Traveller;
import com.wandr.backend.enums.Role;
import com.wandr.backend.service.TravellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;

@Service
public class TravellerServiceImpl implements TravellerService {

    private final TravellerDAO travellerDAO;

    private static final Logger logger = LoggerFactory.getLogger(TravellerServiceImpl.class);

    @Autowired
    public TravellerServiceImpl(TravellerDAO travellerDAO) {
        this.travellerDAO = travellerDAO;
    }
    @Override
    public ApiResponse<Void> updateTravellerJwt (String jwt, Long travellerId) {
        travellerDAO.updateTravellerJwt(jwt, travellerId);
        return new ApiResponse<>(true, 200, "JWT updated successfully");
    }

    @Override
        public String getSalt(String email){
        Optional<Traveller> travellerOpt = travellerDAO.findByEmail(email);
        if (travellerOpt.isEmpty()) {
            return null;
        }
        Traveller traveller = travellerOpt.get();
        return traveller.getSalt();
    }


    @Override
    public ApiResponse<UserDetailsDTO> loginTraveller(UserLoginDTO request) {
        Optional<Traveller> travellerOpt = travellerDAO.findByEmail(request.getEmail());

        if (travellerOpt.isEmpty() || !request.getPassword().equals(travellerOpt.get().getPassword())) {
            logger.error("Invalid email or password for traveller with email: {}", request.getEmail());
            return new ApiResponse<>(false, 401, "Invalid email or password");
        }

        Traveller traveller = travellerOpt.get();
        UserDetailsDTO userDetails = new UserDetailsDTO(
                traveller.getTravellerId(),
                traveller.getEmail(),
                Role.TRAVELLER,
                traveller.getName()
        );

        logger.info("Traveller with email: {} logged in successfully", request.getEmail());
        return new ApiResponse<>(true, 200, "Traveller login successful", userDetails);
    }


    @Override
    public ApiResponse<UserDetailsDTO> registerTraveller(TravellerSignupDTO request) {
        if (travellerDAO.existsByEmail(request.getEmail())) {
            return new ApiResponse<>(false, 400, "Email already in use");
        }

        Traveller traveller = new Traveller();
        traveller.setName(request.getName());
        traveller.setEmail(request.getEmail());
        traveller.setPassword(request.getPassword());
        traveller.setCountry(request.getCountry());
        traveller.setCategories(Collections.emptyList()); // Initially empty
        traveller.setActivities(Collections.emptyList()); // Initially empty
        traveller.setProfileImage(""); // Initially empty
        traveller.setSalt(request.getSalt());
        traveller.setCreatedAt(new Timestamp(System.currentTimeMillis()));



        travellerDAO.save(traveller);

        Optional<Traveller> travellerOpt = travellerDAO.findByEmail(request.getEmail());
        Traveller travellerData = travellerOpt.get();
        UserDetailsDTO userDetails = new UserDetailsDTO(
                travellerData.getTravellerId(),
                travellerData.getEmail(),
                Role.TRAVELLER,
                travellerData.getName()
        );

        return new ApiResponse<>(true, 201, "Traveller registered successfully", userDetails);
    }

    @Override
    public ApiResponse<String> updateCategories(Long travellerId, UpdateCategoriesDTO request) {
        travellerDAO.updateCategories(travellerId, request.getCategories());
        return new ApiResponse<>(true, 200, "Categories updated successfully");
    }

    @Override
    public ApiResponse<String> updateActivities(Long travellerId, UpdateActivitiesDTO request) {
        travellerDAO.updateActivities(travellerId, request.getActivities());
        return new ApiResponse<>(true, 200, "Activities updated successfully");
    }

    @Override
    public ApiResponse<String> updateProfile(Long travellerId, UpdateProfileDTO request) {
        Traveller existingTraveller = travellerDAO.findById(travellerId);
        if (existingTraveller == null) {
            return new ApiResponse<>(false, 404, "Traveller not found");
        }

        if (request.getName() != null) {
            existingTraveller.setName(request.getName());
        }
        if (request.getEmail() != null) {
            existingTraveller.setEmail(request.getEmail());
        }
        if (request.getCountry() != null) {
            existingTraveller.setCountry(request.getCountry());
        }
        if (request.getCategories() != null) {
            existingTraveller.setCategories(request.getCategories());
        }
        if (request.getActivities() != null) {
            existingTraveller.setActivities(request.getActivities());
        }
        if (request.getProfileImage() != null) {
            existingTraveller.setProfileImage(request.getProfileImage());
        }

        travellerDAO.updateProfile(existingTraveller);

        return new ApiResponse<>(true, 200, "Profile updated successfully");
    }


}
