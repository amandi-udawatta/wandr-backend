package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/update")
    public ApiResponse<Void> updatePlaces(@RequestParam String location, @RequestParam(defaultValue = "50000") int radius, @RequestParam(defaultValue = "10") int maxResults) {
        return placeService.updatePlaces(location, radius, maxResults);
    }

    @GetMapping("/get-category")
    public ApiResponse<Void> getPlaceCategories(@RequestParam Long placeId) {
        return placeService.getPlaceCategories(placeId);
    }

    @GetMapping("/get-activity")
    public ApiResponse<Void> getPlaceActivities(@RequestParam Long placeId) {
        return placeService.getPlaceActivities(placeId);
    }

    @GetMapping("/bulk-categorize")
    public ApiResponse<Void> bulkCategorizePlaces() {
        return placeService.bulkCategorizePlaces();
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPlace(@RequestParam String name) {
        Map<String, Object> place = placeService.searchPlaceByName(name);
        return ResponseEntity.ok(place);
    }

    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getPlaceDetails(@RequestParam String placeId) {
        Map<String, Object> placeDetails = placeService.getPlaceDetails(placeId);
        return ResponseEntity.ok(placeDetails);
    }


}
