package com.wandr.backend.service.impl;

import com.wandr.backend.dao.ActivityDAO;
import com.wandr.backend.dao.CategoryDAO;
import com.wandr.backend.dao.PlaceDAO;
import com.wandr.backend.dto.place.PlaceDTO;
import com.wandr.backend.dto.recommendation.RecommendationRequestDTO;
import com.wandr.backend.dto.recommendation.RecommendationResponseDTO;
import com.wandr.backend.dto.recommendation.RecommendedPlaceDTO;
import com.wandr.backend.dto.recommendation.UserDTO;
import com.wandr.backend.dto.recommendation.PlacesDTO;
import com.wandr.backend.dto.traveller.TravellerDTO;
import com.wandr.backend.entity.Activity;
import com.wandr.backend.entity.Category;
import com.wandr.backend.entity.Places;
import com.wandr.backend.entity.Traveller;
import com.wandr.backend.service.PlaceService;
import com.wandr.backend.service.RecommendationService;
import com.wandr.backend.service.TravellerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Value("${recommendation.api.url}")
    private String recommendationApiUrl;
    private final CategoryDAO categoryDAO;
    private final ActivityDAO activityDAO;

    private final PlaceDAO placeDAO;

    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final RestTemplate restTemplate;
    private final TravellerService travellerService;
    private final PlaceService placeService;

    public RecommendationServiceImpl(TravellerService travellerService, PlaceService placeService, CategoryDAO categoryDAO, ActivityDAO activityDAO, PlaceDAO placeDAO) {
        this.categoryDAO = categoryDAO;
        this.activityDAO = activityDAO;
        this.placeDAO = placeDAO;
        this.travellerService = travellerService;
        this.placeService = placeService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<RecommendedPlaceDTO> getRecommendedPlaces(Long travellerId) {
        TravellerDTO traveller = travellerService.getById(travellerId);
        List<PlaceDTO> allPlaces = placeService.getAllPlaces();
        //map allPlaces to placesDTO
        List<PlacesDTO> allPlacesDTO = allPlaces.stream().map(this::mapPlaceDTOToPlacesDTO).collect(Collectors.toList());

        // Convert category and activity IDs to names
        List<String> travellerCategories = categoryDAO.findByCategoryIds(traveller.getCategories()).stream().map(Category::getName).collect(Collectors.toList());
        List<String> travellerActivities = activityDAO.findByActivityIds(traveller.getActivities()).stream().map(Activity::getName).collect(Collectors.toList());

        // Create request DTO
        RecommendationRequestDTO requestDTO = new RecommendationRequestDTO();
        requestDTO.setUser(new UserDTO(travellerId, travellerCategories, travellerActivities));
        requestDTO.setPlaces(allPlacesDTO);

        // Call recommendation API
        ResponseEntity<List<RecommendationResponseDTO>> responseEntity = restTemplate.exchange(
                recommendationApiUrl,
                HttpMethod.POST,
                new HttpEntity<>(requestDTO),
                new ParameterizedTypeReference<List<RecommendationResponseDTO>>() {}
        );

        List<RecommendationResponseDTO> response = responseEntity.getBody();

        // Process response and map to RecommendedPlaceDTO
        return response.stream()
                .filter(r -> r.getPlaceId() != null)
                .map(r -> mapToRecommendedPlaceDTO(r, travellerId))
                .collect(Collectors.toList());
    }

    private PlacesDTO mapPlaceDTOToPlacesDTO (PlaceDTO placedto){
        PlacesDTO dto = new PlacesDTO();
        dto.setName(placedto.getName());
        dto.setId(placedto.getId());
        dto.setActivities(placedto.getActivities());
        dto.setCategories(placedto.getCategories());
        return dto;
    }



    private RecommendedPlaceDTO mapToRecommendedPlaceDTO(RecommendationResponseDTO responseDTO, Long travellerId) {
        PlaceDTO place = placeService.getPlaceById(responseDTO.getPlaceId());
        RecommendedPlaceDTO dto = new RecommendedPlaceDTO();
        dto.setId(place.getId());
        dto.setName(place.getName());
        dto.setDescription(place.getDescription());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());
        dto.setAddress(place.getAddress());
        dto.setImage(place.getImage());

        dto.setCategories(place.getCategories());
        dto.setActivities(place.getActivities());
        dto.setLiked(placeDAO.isPlaceLikedByTraveller(place.getId(), travellerId));
        dto.setSimilarity(new BigDecimal(responseDTO.getSimilarity()));
        return dto;
    }


    }
