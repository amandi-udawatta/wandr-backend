package com.wandr.bff.controller;

import com.wandr.bff.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class JwtValidationController {

    @Autowired
    private JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(JwtValidationController.class);

    @PostMapping("/validate-jwt")
    public ResponseEntity<String> validateJwt(@RequestHeader("Authorization") String token) {
        try {
            Claims claims = jwtService.validateJwtToken(token.trim().replace("Bearer ", ""));
            logger.info("JWT token is valid");
            return ResponseEntity.ok("Valid JWT token");
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
        } catch (UnsupportedJwtException e) {
            logger.warn("Unsupported JWT token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unsupported JWT token");
        } catch (MalformedJwtException e) {
            logger.warn("Malformed JWT token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Malformed JWT token");
        } catch (SignatureException e) {
            logger.warn("Invalid JWT signature", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT claims string is empty.");
        }
    }
}
