package com.wandr.bff.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandr.bff.DTO.ApiResponse;
import com.wandr.bff.DTO.TokenResponse;
import com.wandr.bff.service.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    @Value("${core.backend.url}")
    private String coreBackendUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProxyController.class);

    // Method to validate token
    private boolean validateToken(String token) {
        try {
            jwtService.validateJwtToken(token);
            logger.info("JWT token is valid");
            return true;
        } catch (SignatureException | MalformedJwtException e) {
            logger.error("JWT validation error: ", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during JWT validation: ", e);
            return false;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginDetails) {
        String loginUrl = "";
        switch (loginDetails.get("role")) {
            case "TRAVELLER":
                loginUrl = coreBackendUrl + "/api/travellers/login";
                break;
            case "ADMIN":
                loginUrl = coreBackendUrl + "/api/admins/login";
                break;
            case "BUSINESS":
                loginUrl = coreBackendUrl + "/api/businesses/login";
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, HttpStatus.BAD_REQUEST.value(), "Invalid role", null));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginDetails, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> userDetails = (Map<String, Object>) response.getBody().get("data");
                if (userDetails != null) {
                    Long id = Long.valueOf(userDetails.get("id").toString());
                    String role = userDetails.get("role").toString();
                    String email = userDetails.get("email").toString();
                    String name = userDetails.get("name").toString();

                    String accessToken = jwtService.createJwtToken(id, role, email, name);
                    String refreshToken = jwtService.createRefreshToken(id, role, email, name);


                    logger.info("Successfully created JWT token for user with email: {}", email);

                    // Send refresh token to backend for it to save
                    String saveRefreshTokenUrl;
                    switch (role) {
                        case "TRAVELLER":
                            saveRefreshTokenUrl = coreBackendUrl + "/api/travellers/save-jwt";
                            break;
                        case "ADMIN":
                            saveRefreshTokenUrl = coreBackendUrl + "/api/admins/save-jwt";
                            break;
                        case "BUSINESS":
                            saveRefreshTokenUrl = coreBackendUrl + "/api/businesses/save-jwt";
                            break;
                        default:
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(new ApiResponse<>(false, HttpStatus.BAD_REQUEST.value(), "Invalid role", null));
                    }

                    Map<String, Object> saveTokenRequestBody = Map.of("travellerId", id, "jwtToken", refreshToken);

                    HttpEntity<Map<String, Object>> saveTokenEntity = new HttpEntity<>(saveTokenRequestBody, headers);

                    ResponseEntity<Map<String, Object>> saveTokenResponse = restTemplate.exchange(saveRefreshTokenUrl, HttpMethod.POST, saveTokenEntity, new ParameterizedTypeReference<>() {});

                    // Create token response
                    TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
                    ApiResponse<TokenResponse> tokenApiResponse = new ApiResponse<>(true, HttpStatus.OK.value(), "Successfully logged in", tokenResponse);
                    return ResponseEntity.ok(tokenApiResponse);
                }
            }
            return ResponseEntity.status(response.getStatusCode())
                    .body(new ApiResponse<>(false, response.getStatusCodeValue(), "Login failed", response.getBody()));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error during login: ", e);
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(false, e.getStatusCode().value(), e.getResponseBodyAsString(), null));
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> signupDetails) {
        String signUpUrl = "";
        switch (signupDetails.get("role")) {
            case "TRAVELLER":
                signUpUrl = coreBackendUrl + "/api/travellers/signup";
                break;
            case "ADMIN":
                signUpUrl = coreBackendUrl + "/api/admins/signup";
                break;
            case "BUSINESS":
                signUpUrl = coreBackendUrl + "/api/businesses/signup";
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, HttpStatus.BAD_REQUEST.value(), "Invalid role", null));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(signupDetails, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(signUpUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> userDetails = (Map<String, Object>) response.getBody().get("data");
                if (userDetails != null) {
                    Long id = Long.valueOf(userDetails.get("id").toString());
                    String role = userDetails.get("role").toString();
                    String email = userDetails.get("email").toString();
                    String name = userDetails.get("name").toString();

                    String accessToken = jwtService.createJwtToken(id, role, email, name);
                    String refreshToken = jwtService.createRefreshToken(id, role, email, name);


                    logger.info("Successfully created JWT token for user with email: {}", email);

                    // Send refresh token to backend for it to save
                    String saveRefreshTokenUrl;
                    switch (role) {
                        case "TRAVELLER":
                            saveRefreshTokenUrl = coreBackendUrl + "/api/travellers/save-jwt";
                            break;
                        case "ADMIN":
                            saveRefreshTokenUrl = coreBackendUrl + "/api/admins/save-jwt";
                            break;
                        case "BUSINESS":
                            saveRefreshTokenUrl = coreBackendUrl + "/api/businesses/save-jwt";
                            break;
                        default:
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(new ApiResponse<>(false, HttpStatus.BAD_REQUEST.value(), "Invalid role", null));
                    }

                    Map<String, Object> saveTokenRequestBody = Map.of("travellerId", id, "jwtToken", refreshToken);

                    HttpEntity<Map<String, Object>> saveTokenEntity = new HttpEntity<>(saveTokenRequestBody, headers);

                    ResponseEntity<Map<String, Object>> saveTokenResponse = restTemplate.exchange(saveRefreshTokenUrl, HttpMethod.POST, saveTokenEntity, new ParameterizedTypeReference<>() {});

                    // Create token response
                    TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
                    ApiResponse<TokenResponse> tokenApiResponse = new ApiResponse<>(true, HttpStatus.OK.value(), "Successfully signed up", tokenResponse);
                    return ResponseEntity.ok(tokenApiResponse);
                }
            }
            return ResponseEntity.status(response.getStatusCode())
                    .body(new ApiResponse<>(false, response.getStatusCodeValue(), "Sign Up failed", response.getBody()));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error during signup: ", e);
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(false, e.getStatusCode().value(), e.getResponseBodyAsString(), null));
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null));
        }
    }


    @PostMapping("/forward")
    public ResponseEntity<ApiResponse<Object>> forwardRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {

        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Invalid token", null));
        }

        String forwardUrl = coreBackendUrl + request.getRequestURI();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(forwardUrl, HttpMethod.POST, entity, String.class);
            return ResponseEntity.ok(new ApiResponse<>(true, response.getStatusCodeValue(), "Success", response.getBody()));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error forwarding request: ", e);
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(false, e.getStatusCode().value(), e.getResponseBodyAsString(), null));
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null));
        }
    }
}
