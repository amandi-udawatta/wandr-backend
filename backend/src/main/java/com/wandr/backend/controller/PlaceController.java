package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.place.PlaceDTO;
import com.wandr.backend.dto.place.UpdatePlaceDTO;
import com.wandr.backend.service.PlaceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;
    private static final Logger logger = LoggerFactory.getLogger(PlaceController.class);

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/fill-database")
    public ApiResponse<Void> fillDatabase(@RequestParam String location, @RequestParam(defaultValue = "50000") int radius, @RequestParam(defaultValue = "10") int maxResults) {
        return placeService.fillDatabase(location, radius, maxResults);
    }

    @GetMapping("/categorize")
    public ApiResponse<Void> getPlaceCategories(@RequestParam Long placeId) {
        return placeService.getPlaceCategories(placeId);
    }

    @GetMapping("/bulk-categorize")
    public ApiResponse<Void> bulkCategorizePlaces() {
        return placeService.bulkCategorizePlaces();
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPlace(@RequestParam String name) {
        Map<String, Object> place = placeService.searchPlaceByNameFromAPI(name);
        return ResponseEntity.ok(place);
    }

    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getPlaceDetails(@RequestParam String placeId) {
        Map<String, Object> placeDetails = placeService.getPlaceDetailsFromAPI(placeId);
        return ResponseEntity.ok(placeDetails);
    }

    @GetMapping
    public ApiResponse<List<PlaceDTO>> getAllPlaces() {
        try {
            List<PlaceDTO> places = placeService.getAllPlaces();
            logger.info("Successfully retrieved all places");
            return new ApiResponse<>(true, HttpStatus.OK.value(), "All places retrieved successfully", places);
        } catch (Exception e) {
            logger.error("Error retrieving all places: {}", e.getMessage(), e);
            return new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving all places", null);
        }
    }

    @PostMapping("/update/{placeId}")
    public ApiResponse<PlaceDTO> update(@PathVariable long placeId, @RequestBody UpdatePlaceDTO updatePlaceDTO) {

        try {
            PlaceDTO updatedPlace = placeService.update(placeId, updatePlaceDTO);
            logger.info("Successfully updated place with id: {}", placeId);
            return new ApiResponse<>(true, HttpStatus.OK.value(), "Place updated successfully", updatedPlace);
        } catch (Exception e) {
            logger.error("Error updating place with id {}: {}", placeId, e.getMessage(), e);
            return new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating place", null);
        }
    }

    @DeleteMapping("delete/{placeId}")
    public ApiResponse<Void> delete(@PathVariable long placeId) {
        try {
            placeService.delete(placeId);
            logger.info("Successfully deleted place with id: {}", placeId);
            return new ApiResponse<>(true, HttpStatus.OK.value(), "Place deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting place with id {}: {}", placeId, e.getMessage(), e);
            return new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting place");
        }
    }


}
