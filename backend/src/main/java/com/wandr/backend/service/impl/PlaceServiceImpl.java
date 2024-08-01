package com.wandr.backend.service.impl;

import com.wandr.backend.dao.ActivityDAO;
import com.wandr.backend.dao.PlaceDAO;
import com.wandr.backend.dao.CategoryDAO;
import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.place.DashboardPlaceDTO;
import com.wandr.backend.dto.place.PlaceDTO;
import com.wandr.backend.dto.place.UpdatePlaceDTO;
import com.wandr.backend.entity.Category;
import com.wandr.backend.entity.Places;
import com.wandr.backend.entity.Activity;
import com.wandr.backend.service.PlaceService;
import com.wandr.backend.util.FileUploadUtil;
import com.wandr.backend.util.ByteArrayMultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaceServiceImpl implements PlaceService {

    private final PlaceDAO placeDAO;
    private final CategoryDAO categoryDAO;
    private final ActivityDAO activityDAO;
    private final RestTemplate restTemplate;

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.places.api.url}")
    private String apiUrl;

    @Value("${wikipedia.api.url}")
    private String wikipediaApiUrl;

    @Value("${wikipedia.search.url}")
    private String wikipediaSearchUrl;

    @Value("${OPENAI_API_KEY}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Value("${core.backend.url}")
    private String backendUrl;

    private static final Logger logger = LoggerFactory.getLogger(PlaceServiceImpl.class);
    public PlaceServiceImpl(PlaceDAO placeDAO, CategoryDAO categoryDAO, ActivityDAO activityDAO, RestTemplate restTemplate) {
        this.placeDAO = placeDAO;
        this.categoryDAO = categoryDAO;
        this.activityDAO = activityDAO;
        this.restTemplate = restTemplate;
    }

    @Override
    public PlaceDTO getPlaceById(long placeId) {
        Places place = placeDAO.findById(placeId);
        if (place == null) {
            logger.warn("Place not found with id: {}", placeId);
            throw new IllegalArgumentException("Place not found with id: " + placeId);
        }
        PlaceDTO placeDTO = placeToPlaceDTO(place);
        return placeDTO;
    }

    //get all places
    @Override
    public List<PlaceDTO> getAllPlaces() {
        List<Places> places = placeDAO.findAll();
        return places.stream()
                .map(place -> {
                    PlaceDTO placeDTO = placeToPlaceDTO(place);
                    return placeDTO;
                })
                .collect(Collectors.toList());
    }


    @Override
    public PlaceDTO update(long placeId, UpdatePlaceDTO updatePlaceDTO) {
        try {
            Places place = placeDAO.findById(placeId);
            if (place == null) {
                logger.warn("Place not found with id: {}", placeId);
                throw new IllegalArgumentException("Place not found with id: " + placeId);
            }
            // Update only the fields that are provided in the DTO
            if (updatePlaceDTO.getName() != null) {
                place.setName(updatePlaceDTO.getName());
            }
            if (updatePlaceDTO.getDescription() != null) {
                place.setDescription(updatePlaceDTO.getDescription());
            }
            if (updatePlaceDTO.getAddress() != null) {
                place.setAddress(updatePlaceDTO.getAddress());
            }
            placeDAO.update(place);
            logger.info("Successfully updated place with id: {}", placeId);
            //return newly updated place details
            return getPlaceById(placeId);

        } catch (Exception e) {
            logger.error("Error updating place with id: {}", placeId, e);
            throw e;
        }
    }


    //delete place
    @Override
    public ApiResponse<Void> delete(long placeId) {
        if (placeDAO.findById(placeId) == null) {
            return new ApiResponse<>(false, 404, "Place not found");
        }
        try {
            placeDAO.delete(placeId);
            return new ApiResponse<>(true, 200, "Place deleted successfully");
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Error deleting place");
        }
    }

    @Override
    public ApiResponse<Void> fillDatabase(String location, int radius, int maxResults) {
        String url = String.format("%s?location=%s&radius=%d&type=tourist_attraction&key=%s", apiUrl, location, radius, apiKey);
        fetchAndSavePlaces(url, maxResults);
        return new ApiResponse<>(true, 200, "Places updated successfully");
    }

    public void fetchAndSavePlaces(String url, int maxResults) {
        int resultsCount = 0;
        while (url != null && resultsCount < maxResults) {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> places = (List<Map<String, Object>>) response.get("results");


                for (Map<String, Object> placeData : places) {
                    List<String> types = (List<String>) placeData.get("types");
                    if (types == null || !types.contains("tourist_attraction") || types.contains("lodging")) {
                        continue;
                    }

                    if (resultsCount >= maxResults) break;
                    Places place = new Places();
                    place.setName((String) placeData.get("name"));
                    place.setDescription(fetchPlaceDescription((String) placeData.get("name")));
                    Map<String, Object> geometry = (Map<String, Object>) placeData.get("geometry");
                    Map<String, Object> locationData = (Map<String, Object>) geometry.get("location");
                    place.setLatitude((double) locationData.get("lat"));
                    place.setLongitude((double) locationData.get("lng"));
                    place.setAddress((String) placeData.get("vicinity"));

                    // Store photo_reference
                    if (placeData.containsKey("photos")) {
                        List<Map<String, Object>> photos = (List<Map<String, Object>>) placeData.get("photos");
                        if (!photos.isEmpty()) {
                            String photoReference = (String) photos.get(0).get("photo_reference");
                            // Fetch and save the photo
                            savePlacePhoto(photoReference, place);
                        }
                    }
                    placeDAO.save(place);


                    resultsCount++;
                }

                if (response.containsKey("next_page_token")) {
                    String nextPageToken = (String) response.get("next_page_token");
                    url = apiUrl + "?pagetoken=" + nextPageToken + "&key=" + apiKey;
                    try {
                        Thread.sleep(2000); // wait for a few seconds before making the next request
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    url = null;
                }
            } else {
                url = null;
            }
        }
    }

    private void savePlacePhoto(String photoReference, Places place) {
        String photoUrl = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=%s", photoReference, apiKey);
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(photoUrl, byte[].class);
            if (response.getStatusCode() == HttpStatus.OK) {
                byte[] imageBytes = response.getBody();
                if (imageBytes != null) {
                    String fileName = place.getName().replaceAll("\\s+", "_") + ".jpg";
                    MultipartFile multipartFile = new ByteArrayMultipartFile("file", fileName, "image/jpeg", imageBytes);
                    String savedFileName = FileUploadUtil.saveFile(multipartFile, "places");
                    place.setImage(savedFileName); // Assuming the Places entity has an image field
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch or save photo for place: " + place.getName(), e);
        }
    }

    private String fetchPlaceDescription(String placeName) {
        String formattedTitle = placeName.replace(" ", "_");
        String wikipediaUrl = wikipediaApiUrl + formattedTitle;
        try {
            Map<String, Object> response = restTemplate.getForObject(wikipediaUrl, Map.class);
            if (response != null && response.containsKey("extract")) {
                return (String) response.get("extract");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                // Try a simplified title if the exact title is not found
                System.out.println("Page not found for title: " + formattedTitle);
                String simplifiedTitle = placeName.split("\\(")[0].trim().replace(" ", "_");
                wikipediaUrl = wikipediaApiUrl + simplifiedTitle;
                try {
                    Map<String, Object> response = restTemplate.getForObject(wikipediaUrl, Map.class);
                    if (response != null && response.containsKey("extract")) {
                        return (String) response.get("extract");
                    }
                } catch (HttpClientErrorException ex) {
                    if (ex.getStatusCode().value() == 404) {
                        // Perform a search as a last resort
                        System.out.println("Page not found for simplified title: " + simplifiedTitle);
                        String searchUrl = wikipediaSearchUrl + placeName.replace(" ", "%20");
                        try {
                            Map<String, Object> searchResponse = restTemplate.getForObject(searchUrl, Map.class);
                            if (searchResponse != null && searchResponse.containsKey("query")) {
                                Map<String, Object> query = (Map<String, Object>) searchResponse.get("query");
                                if (query.containsKey("search")) {
                                    List<Map<String, Object>> searchResults = (List<Map<String, Object>>) query.get("search");
                                    if (!searchResults.isEmpty()) {
                                        String searchTitle = (String) searchResults.get(0).get("title");
                                        wikipediaUrl = wikipediaApiUrl + searchTitle.replace(" ", "_");
                                        searchResponse = restTemplate.getForObject(wikipediaUrl, Map.class);
                                        if (searchResponse != null && searchResponse.containsKey("extract")) {
                                            return (String) searchResponse.get("extract");
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex1) {
                            System.out.println("Error performing search: " + ex1.getMessage());
                        }
                    }
                }
            }
        }
        return "No detailed description available.";
    }



    // categorize places into category and activity
    public ApiResponse<Void> getPlaceCategories(Long placeId) {
        Places place = placeDAO.findById(placeId);

        List<String> categoriesForPlace = getCategoriesForPlace(place);
        List<String> activitiesForPlace = getActivitiesForPlace(place);

        List<Long> categoryIds = categoriesForPlace.stream()
                .map(categoryDAO::findByName)
                .filter(Objects::nonNull)
                .map(Category::getId)
                .collect(Collectors.toList());

        List<Long> activityIds = activitiesForPlace.stream()
                .map(activityDAO::findByName)
                .filter(Objects::nonNull)
                .map(Activity::getId)
                .collect(Collectors.toList());

        placeDAO.updateCategories(placeId, categoryIds);
        placeDAO.updateActivities(placeId, activityIds);

        return new ApiResponse<>(true, 200, "Places categorized successfully");
    }


//categorize places into categories
    private List<String> getCategoriesForPlace(Places place) {
        List<Category> categories = categoryDAO.findAll();
        String categoriesStr = categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));

        String prompt = String.format(
                "Categorize the following tourist attraction place in Sri Lanka into 3 of the following categories. Return only the category names, separated by commas: %s. The place name is: %s. located in: %s",
                categoriesStr, place.getName(), place.getAddress());


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", Arrays.asList(
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", "You are a helpful assistant that categorizes places.");
                }},
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", prompt);
                }}
        ));
        requestBody.put("max_tokens", 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(openAiApiUrl, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String text = (String) message.get("content");
                    return Arrays.stream(text.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }

    private List<String> getActivitiesForPlace(Places place) {
        List<Activity> activities = activityDAO.findAll();
        String activitiesStr = activities.stream()
                .map(Activity::getName)
                .collect(Collectors.joining(", "));

        String prompt = String.format(
                "List 1 to 3 activities that can be done at the following tourist attraction place in Sri Lanka. Return only the activity names, separated by commas: %s. The place name is: %s. located in: %s",
                activitiesStr, place.getName(), place.getAddress());


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", Arrays.asList(
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", "You are a helpful assistant that list activities can be done in places.");
                }},
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", prompt);
                }}
        ));
        requestBody.put("max_tokens", 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(openAiApiUrl, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String text = (String) message.get("content");
                    return Arrays.stream(text.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }


    //bulk categorize all places in the db in one function
    public ApiResponse<Void> bulkCategorizePlaces() {
        List<Places> places = placeDAO.findAll();
        for (Places place : places) {
            getPlaceCategories(place.getId());
        }
        return new ApiResponse<>(true, 200, "Bulk categorization completed successfully");
    }


    public Map<String, Object> searchPlaceByNameFromAPI(String placeName) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%s&inputtype=textquery&fields=place_id,name,formatted_address&key=%s", placeName, apiKey);
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        System.out.println("response: " + response);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("candidates")) {
                return ((List<Map<String, Object>>) responseBody.get("candidates")).get(0);
            }
        }
        return null;
    }

    public Map<String, Object> getPlaceDetailsFromAPI(String placeId) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&key=%s", placeId, apiKey);
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("result")) {
                return (Map<String, Object>) responseBody.get("result");
            }
        }
        return null;
    }

    @Override
    public PlaceDTO add(String placeId) {
        Map<String, Object> placeDetails = getPlaceDetailsFromAPI(placeId);
        if (placeDetails == null) {
            logger.warn("Place details not found for placeId: {}", placeId);
            throw new IllegalArgumentException("Place details not found for placeId: " + placeId);
        }

        Places place = new Places();
        place.setName((String) placeDetails.get("name"));
        place.setLatitude((double) ((Map<String, Object>) ((Map<String, Object>) placeDetails.get("geometry")).get("location")).get("lat"));
        place.setLongitude((double) ((Map<String, Object>) ((Map<String, Object>) placeDetails.get("geometry")).get("location")).get("lng"));
        place.setAddress((String) placeDetails.get("formatted_address"));

        // Store photo_reference
        if (placeDetails.containsKey("photos")) {
            List<Map<String, Object>> photos = (List<Map<String, Object>>) placeDetails.get("photos");
            if (!photos.isEmpty()) {
                String photoReference = (String) photos.get(0).get("photo_reference");
                // Fetch and save the photo
                savePlacePhoto(photoReference, place);
            }
        }

        Long id = placeDAO.save(place);
        Places addedPlace = placeDAO.findById(id);
        System.out.println("added place: " + addedPlace);

        // Generate description, categories, and activities
        String description = generateDescription(addedPlace.getName(), addedPlace.getAddress());
        placeDAO.updateDescription(addedPlace.getId(), description);
        getPlaceCategories(id);

        //return newly added place details
        return getPlaceById(id);

    }


    private String generateDescription(String name, String address) {
        String prompt = String.format(
                "Give a 50 word description for following tourist attraction place in Sri Lanka for a travel app. Return only the description.The place name is: %s. located in: %s",
                name,address);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", Arrays.asList(
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", "You are a helpful assistant that generate descriptions for places.");
                }},
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", prompt);
                }}
        ));
        requestBody.put("max_tokens", 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(openAiApiUrl, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String description = (String) message.get("content");
                    return description;
                }
            }
        }
        return ("No description found");
    }


    //place to placeDTO
    private PlaceDTO placeToPlaceDTO(Places place) {
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setId(place.getId());
        placeDTO.setName(place.getName());
        placeDTO.setDescription(place.getDescription());
        placeDTO.setLatitude(place.getLatitude());
        placeDTO.setLongitude(place.getLongitude());
        placeDTO.setAddress(place.getAddress());
        placeDTO.setImage(place.getImage());
        List<Category> categories = categoryDAO.findByCategoryIds(place.getCategories());
        List<Activity> activities = activityDAO.findByActivityIds(place.getActivities());
        //get a list of category and activity names
        List<String> categoryNames = categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        List<String> activityNames = activities.stream()
                .map(Activity::getName)
                .collect(Collectors.toList());
        placeDTO.setCategories(categoryNames);
        placeDTO.setActivities(activityNames);
        return placeDTO;
    }

}

