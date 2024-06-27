package com.wandr.bff.controller;

import com.wandr.bff.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TokenController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/generate-token")
    public String generateToken(@RequestBody Map<String, Object> userInfo) {
        Long id = Long.valueOf(userInfo.get("id").toString());
        String role = userInfo.get("role").toString();
        String email = userInfo.get("email").toString();
        String name = userInfo.get("name").toString();

        return jwtService.createJwtToken(id, role, email, name);
    }

    @PostMapping("/generate-refresh-token")
    public String generateRefreshToken(@RequestBody Map<String, Object> userInfo) {
        Long id = Long.valueOf(userInfo.get("id").toString());
        String role = userInfo.get("role").toString();
        String email = userInfo.get("email").toString();
        String name = userInfo.get("name").toString();

        return jwtService.createRefreshToken(id, role, email, name);
    }

    @PostMapping("/refresh-token")
    public String refreshToken(@RequestBody String refreshToken) {
        // Validate and parse refresh token
        Claims claims = jwtService.validateJwtToken(refreshToken);

        Long id = claims.get("id", Long.class);
        String role = claims.get("role", String.class);
        String email = claims.get("email", String.class);
        String name = claims.get("name", String.class);

        // Generate new access token
        return jwtService.createJwtToken(id, role, email, name);
    }
}
