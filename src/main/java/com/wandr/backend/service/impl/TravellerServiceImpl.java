package com.wandr.backend.service.impl;

import com.wandr.backend.dao.TravellerDAO;
import com.wandr.backend.dto.*;
import com.wandr.backend.entity.Traveller;
import com.wandr.backend.service.TravellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;

@Service
public class TravellerServiceImpl implements TravellerService {

    private final TravellerDAO travellerDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(TravellerServiceImpl.class);

    @Autowired
    public TravellerServiceImpl(TravellerDAO travellerDAO, BCryptPasswordEncoder passwordEncoder) {
        this.travellerDAO = travellerDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResponse<String> loginTraveller(TravellerLoginDTO request) {
        Optional<Traveller> travellerOpt = travellerDAO.findByEmail(request.getEmail());

        if (travellerOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), travellerOpt.get().getPassword())) {
            logger.error("Invalid email or password for traveller with email: {}", request.getEmail());
            return new ApiResponse<>(false, 401, "Invalid email or password");
        }
        logger.info("Traveller with email: {} logged in successfully", request.getEmail());
        return new ApiResponse<>(true, 200, "Traveller Login successful");

    }

    @Override
    public ApiResponse<String> registerTraveller(TravellerSignupDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new ApiResponse<>(false, 400, "Passwords do not match");
        }

        if (travellerDAO.existsByEmail(request.getEmail())) {
            return new ApiResponse<>(false, 400, "Email already in use");
        }

        Traveller traveller = new Traveller();
        traveller.setName(request.getName());
        traveller.setEmail(request.getEmail());
        traveller.setPassword(passwordEncoder.encode(request.getPassword()));
        traveller.setCountry(request.getCountry());
        traveller.setCategories(Collections.emptyList()); // Initially empty
        traveller.setActivities(Collections.emptyList()); // Initially empty
        traveller.setProfileImage(""); // Initially empty

        travellerDAO.save(traveller);

        return new ApiResponse<>(true, 201, "Traveller registered successfully");
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
