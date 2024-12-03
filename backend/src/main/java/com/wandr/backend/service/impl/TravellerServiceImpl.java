package com.wandr.backend.service.impl;

import com.wandr.backend.dao.ActivityDAO;
import com.wandr.backend.dao.CategoryDAO;
import com.wandr.backend.dao.PlaceDAO;
import com.wandr.backend.dao.TravellerDAO;
import com.wandr.backend.dto.*;
import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.dto.place.PlaceDTO;
import com.wandr.backend.dto.recommendation.*;
import com.wandr.backend.dto.traveller.*;
import com.wandr.backend.entity.Activity;
import com.wandr.backend.entity.Category;
import com.wandr.backend.entity.Traveller;
import com.wandr.backend.enums.Role;
import com.wandr.backend.service.PlaceService;
import com.wandr.backend.service.TravellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TravellerServiceImpl implements TravellerService {

    private final TravellerDAO travellerDAO;
    private final PlaceDAO placeDAO;
    private final ActivityDAO activityDAO;
    private final CategoryDAO categoryDAO;

    private final PlaceService placeService;

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TravellerServiceImpl.class);

    @Value("${recommendation.api.url}")
    private String recommendationApiUrl;

    @Autowired
    public TravellerServiceImpl(TravellerDAO travellerDAO, PlaceDAO placeDAO, ActivityDAO activityDAO, CategoryDAO categoryDAO, PlaceService placeService) {
        this.travellerDAO = travellerDAO;
        this.placeDAO = placeDAO;
        this.activityDAO = activityDAO;
        this.categoryDAO = categoryDAO;
        this.placeService = placeService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ApiResponse<Void> updateTravellerJwt(String jwt, Long travellerId) {
        travellerDAO.updateTravellerJwt(jwt, travellerId);
        return new ApiResponse<>(true, 200, "JWT updated successfully");
    }

    @Override
    public String getSalt(String email) {
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
                traveller.getName(),
                traveller.getMembership()
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

        List<RecommendedPlaceDTO> recommendations = getRecommendedPlaces(travellerData.getTravellerId());
        saveTopRecommendedPlaceIds(travellerData.getTravellerId(), recommendations);
        UserDetailsDTO userDetails = new UserDetailsDTO(
                travellerData.getTravellerId(),
                travellerData.getEmail(),
                Role.TRAVELLER,
                travellerData.getName(),
                travellerData.getMembership()
        );

        return new ApiResponse<>(true, 201, "Traveller registered successfully", userDetails);
    }

    @Override
    public ApiResponse<String> updateCategories(Long travellerId, UpdateCategoriesDTO request) {
        if (travellerDAO.findById(travellerId) == null) {
            return new ApiResponse<>(false, 404, "Traveller not found");
        }
        travellerDAO.updateCategories(travellerId, request.getCategories());
        // Fetch updated recommendations
        //getRecommendedPlaces(travellerId);
        List<RecommendedPlaceDTO> recommendations = getRecommendedPlaces(travellerId);
        saveTopRecommendedPlaceIds(travellerId, recommendations);

        return new ApiResponse<>(true, 200, "Categories updated successfully");
    }

    @Override
    public ApiResponse<String> updateActivities(Long travellerId, UpdateActivitiesDTO request) {
        if (travellerDAO.findById(travellerId) == null) {
            return new ApiResponse<>(false, 404, "Traveller not found");
        }
        travellerDAO.updateActivities(travellerId, request.getActivities());
        // Fetch updated recommendations
        //getRecommendedPlaces(travellerId);
        List<RecommendedPlaceDTO> recommendations = getRecommendedPlaces(travellerId);
        saveTopRecommendedPlaceIds(travellerId, recommendations);

        return new ApiResponse<>(true, 200, "Activities updated successfully");
    }

    @Override
    public ApiResponse<TravellerDTO> updateProfile(Long travellerId, UpdateProfileDTO request) {
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
            List<RecommendedPlaceDTO> recommendations = getRecommendedPlaces(travellerId);
            saveTopRecommendedPlaceIds(travellerId, recommendations);
        }
        if (request.getActivities() != null) {
            existingTraveller.setActivities(request.getActivities());
            List<RecommendedPlaceDTO> recommendations = getRecommendedPlaces(travellerId);
            saveTopRecommendedPlaceIds(travellerId, recommendations);
        }
        if (request.getProfileImage() != null) {
            existingTraveller.setProfileImage(request.getProfileImage());
        }
        if (request.getMembership() != null) {
            existingTraveller.setMembership(request.getMembership());
        }

        travellerDAO.updateProfile(existingTraveller);
        TravellerDTO updatedTraveller = getTravellerById(travellerId);

        //return updated traveller details
        return new ApiResponse<>(true, 200, "Profile updated successfully", updatedTraveller);
    }


    //get traveller by id
    private TravellerDTO getTravellerById(Long travellerId) {
        Traveller traveller = travellerDAO.findById(travellerId);
        if (traveller == null) {
            return null;
        }
        TravellerDTO travellerDTO = travellerToTravellerDTO(traveller);
        return travellerDTO;
    }

    @Override
    public TravellerDTO getById(Long travellerId) {
        Traveller traveller = travellerDAO.findById(travellerId);
        if (traveller == null) {
            return null;
        }
        TravellerDTO travellerDTO = travellerToTravellerDTO(traveller);
        return travellerDTO;
    }

    //traveller to traveller dto
    private TravellerDTO travellerToTravellerDTO(Traveller traveller) {
        TravellerDTO travellerDTO = new TravellerDTO();
        travellerDTO.setTravellerId(traveller.getTravellerId());
        travellerDTO.setName(traveller.getName());
        travellerDTO.setEmail(traveller.getEmail());
        travellerDTO.setCountry(traveller.getCountry());
        travellerDTO.setCategories(traveller.getCategories());
        travellerDTO.setActivities(traveller.getActivities());
        travellerDTO.setProfileImage(traveller.getProfileImage());
        travellerDTO.setCreatedAt(traveller.getCreatedAt());
        travellerDTO.setMembership(traveller.getMembership());
        return travellerDTO;
    }


    //get popular places
    @Override
    public ApiResponse<List<DashboardPlaceDTO>> getPopularPlaces(Long travellerId) {
        List<DashboardPlaceDTO> popularPlaces = travellerDAO.getPopularPlaces(travellerId);
        return new ApiResponse<>(true, 200, "Popular places retrieved", popularPlaces);
    }

    //get favourite places
    @Override
    public ApiResponse<List<DashboardPlaceDTO>> getFavouritePlaces(Long travellerId) {
        List<DashboardPlaceDTO> favouritePlaces = travellerDAO.getFavouritePlaces(travellerId);
        return new ApiResponse<>(true, 200, "Favourite places retrieved", favouritePlaces);
    }

    //get all places for given traveller
    @Override
    public ApiResponse<List<DashboardPlaceDTO>> getPlacesForTraveller(long travellerId) {
        List<DashboardPlaceDTO> allPlaces = travellerDAO.getAllPlaces(travellerId);
        return new ApiResponse<>(true, 200, "All places retrieved", allPlaces);
    }

    //logout traveller
    @Override
    public ApiResponse<Void> logout(Long travellerId) {
        travellerDAO.deleteTravellerJwt(travellerId);
        return new ApiResponse<>(true, 200, "Traveller logged out successfully");
    }


    // Fetch and save recommended places for a traveller
    @Override
    public List<RecommendedPlaceDTO> getRecommendedPlaces(Long travellerId) {
        TravellerDTO traveller = getById(travellerId);

        List<PlaceDTO> allPlaces = placeService.getAllPlaces();
        List<PlacesDTO> allPlacesDTO = allPlaces.stream().map(this::mapPlaceDTOToPlacesDTO).collect(Collectors.toList());

        // Convert category and activity IDs to names
        List<String> travellerCategories = categoryDAO.findByCategoryIds(traveller.getCategories()).stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        List<String> travellerActivities = activityDAO.findByActivityIds(traveller.getActivities()).stream()
                .map(Activity::getName)
                .collect(Collectors.toList());

        // Prepare the recommendation request
        RecommendationRequestDTO requestDTO = new RecommendationRequestDTO();
        requestDTO.setUser(new UserDTO(travellerId, travellerCategories, travellerActivities));
        requestDTO.setPlaces(allPlacesDTO);

        // Call the external recommendation API
        ResponseEntity<List<RecommendationResponseDTO>> responseEntity = restTemplate.exchange(
                recommendationApiUrl,
                HttpMethod.POST,
                new HttpEntity<>(requestDTO),
                new ParameterizedTypeReference<List<RecommendationResponseDTO>>() {}
        );

        List<RecommendationResponseDTO> response = responseEntity.getBody();

        // Map the response to DTOs
        List<RecommendedPlaceDTO> recommendedPlaces = response.stream()
                .filter(r -> r.getPlaceId() != null)
                .map(r -> mapToRecommendedPlaceDTO(r, travellerId))
                .collect(Collectors.toList());

        // Save the top 20 recommendations
        saveTopRecommendedPlaceIds(travellerId, recommendedPlaces);

        return recommendedPlaces;
    }

    // Save the top 20 recommended place IDs to the database
    private void saveTopRecommendedPlaceIds(Long travellerId, List<RecommendedPlaceDTO> recommendedPlaces) {
        List<Long> topPlaceIds = recommendedPlaces.stream()
                .sorted((a, b) -> b.getSimilarity().compareTo(a.getSimilarity())) // Sort by similarity descending
                .limit(20) // Top 20
                .map(RecommendedPlaceDTO::getId)
                .collect(Collectors.toList());

        travellerDAO.saveRecommendedPlaces(travellerId, topPlaceIds);
    }

    // Map PlaceDTO to PlacesDTO for recommendation request
    private PlacesDTO mapPlaceDTOToPlacesDTO(PlaceDTO placeDTO) {
        PlacesDTO dto = new PlacesDTO();
        dto.setId(placeDTO.getId());
        dto.setName(placeDTO.getName());
        dto.setCategories(placeDTO.getCategories());
        dto.setActivities(placeDTO.getActivities());
        return dto;
    }

    // Map recommendation response to RecommendedPlaceDTO
    private RecommendedPlaceDTO mapToRecommendedPlaceDTO(RecommendationResponseDTO responseDTO, Long travellerId) {
        PlaceDTO place = placeService.getPlaceById(responseDTO.getPlaceId());
        RecommendedPlaceDTO dto = new RecommendedPlaceDTO();
        dto.setId(place.getId());
        dto.setName(place.getName());
        dto.setDescription(place.getDescription());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());
        dto.setAddress(place.getAddress());
        dto.setImage("/places/" + place.getImage());
        dto.setCategories(place.getCategories());
        dto.setActivities(place.getActivities());
        dto.setLiked(placeDAO.isPlaceLikedByTraveller(place.getId(), travellerId));
        dto.setSimilarity(new BigDecimal(responseDTO.getSimilarity()));
        return dto;
    }


    @Override
    public ApiResponse<List<DashboardPlaceDTO>> getRecommendedPlacesForDashboard(Long travellerId) {
        List<DashboardPlaceDTO> recommendedPlaces = travellerDAO.getRecommendedPlaces(travellerId);
        return new ApiResponse<>(true, 200, "Recommended places retrieved successfully", recommendedPlaces);
    }





}
