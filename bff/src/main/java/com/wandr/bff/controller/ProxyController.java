package com.wandr.bff.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wandr.bff.DTO.ApiResponse;
import com.wandr.bff.DTO.TokenResponse;
import com.wandr.bff.service.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import com.wandr.bff.util.PasswordUtil;
import org.springframework.web.multipart.MultipartFile;

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
        String userRole = loginDetails.get("role");
        String hashedPassword = loginDetails.get("password");
        String userEmail = loginDetails.get("email");
        String getSaltUrl = coreBackendUrl + "/" + userRole.toLowerCase() + "/get-salt?email=" + userEmail;

        try {
            // Fetch the salt from the backend
            ResponseEntity<ApiResponse<String>> saltResponse = restTemplate.exchange(getSaltUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            logger.info("Salt response: {}", saltResponse);

            // Check if the response is valid and contains the salt
            if (saltResponse.getBody() == null || !saltResponse.getBody().isSuccess() || saltResponse.getBody().getData() == null) {
                logger.error("Failed to retrieve salt for user: {}", userEmail);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, 401, "Please register first to login", null));
            }

            String salt = saltResponse.getBody().getData();
            logger.info("Salt: {}", salt);

            // Encrypt the password with the retrieved salt
            String encryptedPassword = PasswordUtil.encryptPassword(hashedPassword, salt);
            loginDetails.put("password", encryptedPassword);
            loginDetails.remove("role"); // Remove the role field

            logger.info("Encrypted password: {}", encryptedPassword);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginDetails, headers);
            String loginUrl = coreBackendUrl + "/" + userRole.toLowerCase() + "/login";
            logger.info("Login URL: {}", loginUrl);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});
            logger.info("Response: {}", response);

            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> userDetails = (Map<String, Object>) response.getBody().get("data");
                logger.info("User details: {}", userDetails);
                if (userDetails != null) {
                    Long id = Long.valueOf(userDetails.get("id").toString());
                    String role = userDetails.get("role").toString();
                    String email = userDetails.get("email").toString();
                    String name = userDetails.get("name").toString();

                    String accessToken = jwtService.createJwtToken(id, role, email, name);
                    String refreshToken = jwtService.createRefreshToken(id, role, email, name);

                    logger.info("Successfully created JWT token for user with email: {}", email);

                    // Send refresh token to backend for it to save
                    String saveRefreshTokenUrl = coreBackendUrl + "/" + userRole.toLowerCase() + "/save-jwt";

                    Map<String, Object> saveTokenRequestBody = Map.of("userId", id, "jwtToken", refreshToken);

                    HttpEntity<Map<String, Object>> saveTokenEntity = new HttpEntity<>(saveTokenRequestBody, headers);

                    ResponseEntity<Map<String, Object>> saveTokenResponse = restTemplate.exchange(saveRefreshTokenUrl, HttpMethod.POST, saveTokenEntity, new ParameterizedTypeReference<>() {});

                    // Create token response
                    TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
                    ApiResponse<TokenResponse> tokenApiResponse = new ApiResponse<>(true, HttpStatus.OK.value(), "Successfully logged in", tokenResponse);
                    return ResponseEntity.ok(tokenApiResponse);
                }
            }
            return ResponseEntity.status(response.getStatusCode())
                    .body(new ApiResponse<>(false, response.getStatusCodeValue(), "Login failed", null));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error during login: ", e);
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(false, e.getStatusCode().value(), e.getResponseBodyAsString(), null));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Encryption error during login: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Login failed", null));
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Login failed", null));
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> signupDetails) {
        String userRole = signupDetails.get("role");
        String signUpUrl = coreBackendUrl + "/" + userRole.toLowerCase() + "/signup";

        try {
            String hashedPassword = signupDetails.get("password");
            String salt = PasswordUtil.generateSalt();
            System.out.println("Salt: " + salt);
            // Add salt to password before hashing
            String encryptedPassword = PasswordUtil.encryptPassword(hashedPassword, salt);
            signupDetails.put("password", encryptedPassword);
            signupDetails.remove("role"); // Remove the role field
            signupDetails.put("salt", salt);

            System.out.println("signupDetails: " + signupDetails);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(signupDetails, headers);

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
                    String saveRefreshTokenUrl = coreBackendUrl + "/" + userRole.toLowerCase() + "/save-jwt";

                    Map<String, Object> saveTokenRequestBody = Map.of("userId", id, "jwtToken", refreshToken);

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
        } catch (NoSuchAlgorithmException e) {
            logger.error("Encryption error during signup: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Sign Up failed", null));
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Sign Up failed", null));
        }
    }

    @PostMapping("/signup-business")
    public ResponseEntity<?> signup(@RequestParam("name") String name,
                                    @RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    @RequestParam("description") String description,
                                    @RequestParam("longitude") String longitude,
                                    @RequestParam("latitude") String latitude,
                                    @RequestParam("address") String address,
                                    @RequestParam("websiteUrl") String websiteUrl,
                                    @RequestParam("ownerName") String ownerName,
                                    @RequestParam("ownerContact") String ownerContact,
                                    @RequestParam("ownerNic") String ownerNic,
                                    @RequestParam("services") List<String> services,
                                    @RequestParam("languages") List<String> languages,
                                    @RequestParam("businessContact") String businessContact,
                                    @RequestParam("businessType") Integer businessType,
                                    @RequestParam(value = "shopCategory", required = false) Integer shopCategory,
                                    @RequestParam("shopImage") MultipartFile shopImage) {

        String signUpUrl = coreBackendUrl + "/business/signup";

        try {
            // Add salt to hashed password
            String salt = PasswordUtil.generateSalt();
            String encryptedPassword = PasswordUtil.encryptPassword(password, salt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("name", name);
            body.add("email", email);
            body.add("password", encryptedPassword);
            body.add("description", description);
            body.add("address", address);
            body.add("latitude", latitude);
            body.add("longitude", longitude);
            body.add("websiteUrl", websiteUrl);
            body.add("ownerName", ownerName);
            body.add("ownerContact", ownerContact);
            body.add("ownerNic", ownerNic);
            body.add("salt", salt);
            body.add("services", services);
            body.add("languages", languages);
            body.add("businessContact", businessContact);
            body.add("businessType", businessType);

            if (shopCategory != null){
                body.add("shopCategory", shopCategory);
            }

            body.add("shopImage", new ByteArrayResource(shopImage.getBytes()) {
                @Override
                public String getFilename() {
                    return shopImage.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(signUpUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});
            logger.info("Response: " + response);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userDetails = (Map<String, Object>) response.getBody().get("data");
                if (userDetails != null) {
                    Long t_id = Long.valueOf(userDetails.get("id").toString());
                    String t_role = userDetails.get("role").toString();
                    String t_email = userDetails.get("email").toString();
                    String t_name = userDetails.get("name").toString();

                    String accessToken = jwtService.createJwtToken(t_id, t_role, t_email, t_name);
                    String refreshToken = jwtService.createRefreshToken(t_id, t_role, t_email, t_name);

                    logger.info("Successfully created JWT token for user with email: {}", t_email);

                    // Send refresh token to backend for it to save
                    String saveRefreshTokenUrl = coreBackendUrl + "/business/save-jwt";
                    logger.info("saveRefreshTokenUrl: " + saveRefreshTokenUrl);

                    HttpHeaders jsonHeaders = new HttpHeaders();
                    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);

                    Map<String, Object> saveTokenRequestBody = Map.of("userId", t_id, "jwtToken", refreshToken);
                    logger.info("saveTokenRequestBody: " + saveTokenRequestBody);

                    HttpEntity<Map<String, Object>> saveTokenEntity = new HttpEntity<>(saveTokenRequestBody, jsonHeaders);
                    ResponseEntity<Map<String, Object>> saveTokenResponse = restTemplate.exchange(saveRefreshTokenUrl, HttpMethod.POST, saveTokenEntity, new ParameterizedTypeReference<>() {});

                    // Create token response
                    TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
                    ApiResponse<TokenResponse> tokenApiResponse = new ApiResponse<>(true, HttpStatus.OK.value(), "Successfully signed up", tokenResponse);
                    logger.info("tokenApiResponse: " + tokenApiResponse);
                    return ResponseEntity.ok(tokenApiResponse);
                }
            }
            return ResponseEntity.status(response.getStatusCode())
                    .body(new ApiResponse<>(false, response.getStatusCodeValue(), "Sign Up failed", response.getBody()));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error during signup: ", e);
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(false, e.getStatusCode().value(), e.getResponseBodyAsString(), null));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Encryption error during signup: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Sign Up failed", null));
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Sign Up failed", null));
        }
    }



    //handle get requests
    @GetMapping("/forward/**")
    public ResponseEntity<?> forwardGetRequest(
            @RequestHeader("Authorization") String token,
            HttpServletRequest request) {

        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Invalid token", null));
        }

        String requestUri = request.getRequestURI().replace("/api/proxy/forward", "") + "?" + request.getQueryString();
        return forwardRequestWithToken(requestUri, token, null, HttpMethod.GET);
    }

    //handle post requests with JSON body
    @PostMapping("/forward/**")
    public ResponseEntity<?> forwardPostRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {

        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Invalid token", null));
        }

        String requestUri = request.getRequestURI().replace("/api/proxy/forward", "");
        System.out.println("requestUri: " + requestUri);
        return forwardRequestWithToken(requestUri, token, requestBody, HttpMethod.POST);
    }

    //handle post requests with form data
    @PostMapping("/forward-form")
    public ResponseEntity<ApiResponse<Object>> forwardFormPostRequest(
            @RequestHeader("Authorization") String token,
            @RequestParam MultiValueMap<String, String> formData,
            HttpServletRequest request) {

        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Invalid token", null));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
        String requestUri = request.getRequestURI().replace("/api/proxy/forward", "");
        String forwardUrl = coreBackendUrl + requestUri;
        return forwardRequestWithEntity(forwardUrl, entity, HttpMethod.POST);
    }

    //handle put requests
    @PutMapping("/forward/**")
    public ResponseEntity<?> forwardPutRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {

        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Invalid token", null));
        }

        String requestUri = request.getRequestURI().replace("/api/proxy/forward", "");
        return forwardRequestWithToken(requestUri, token, requestBody, HttpMethod.PUT);
    }

    //handle delete requests
    @DeleteMapping("/forward/**")
    public ResponseEntity<?> forwardDeleteRequest(
            @RequestHeader("Authorization") String token,
            HttpServletRequest request) {

        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Invalid token", null));
        }

        String requestUri = request.getRequestURI().replace("/api/proxy/forward", "") + "?" + request.getQueryString();
        return forwardRequestWithToken(requestUri, token, null, HttpMethod.DELETE);
    }


    //helper methods for forward requests
    private ResponseEntity<?> forwardRequestWithToken(
            String requestUri, String token, Object body, HttpMethod method) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(coreBackendUrl + requestUri, method, entity, String.class);

            // Parse the backend response to an object
            ApiResponse<?> backendResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

            return ResponseEntity.ok(new ApiResponse<>(true, backendResponse.getStatusCode(), backendResponse.getMessage(), backendResponse.getData()));
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

    private ResponseEntity<ApiResponse<Object>> forwardRequestWithEntity(
            String forwardUrl, HttpEntity<?> entity, HttpMethod method) {

        try {
            ResponseEntity<String> response = restTemplate.exchange(forwardUrl, method, entity, String.class);
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
