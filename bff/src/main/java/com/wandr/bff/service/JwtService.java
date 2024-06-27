package com.wandr.bff.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${jwt.access.token.expiry}")
    private long ACCESS_TOKEN_EXPIRY;

    @Value("${jwt.refresh.token.expiry}")
    private long REFRESH_TOKEN_EXPIRY;

    public String createJwtToken(Long id, String role, String email, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("role", role);
        claims.put("email", email);
        claims.put("name", name);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey.getBytes())
                .compact();
    }

    public String createRefreshToken(Long id, String role, String email, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("role", role);
        claims.put("email", email);
        claims.put("name", name);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey.getBytes())
                .compact();
    }

    public Claims validateJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}
