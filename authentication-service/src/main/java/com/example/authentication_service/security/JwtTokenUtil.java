package com.example.authentication_service.security;

import com.example.authentication_service.config.JWTConfig;
import com.example.authentication_service.model.UserDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {
    
    @Autowired
    private JWTConfig jwtConfig;

    @SuppressWarnings("deprecation")
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    @SuppressWarnings("deprecation")
    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).build().parseSignedClaims(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, UserDTO userDTO) {
        return (extractUsername(token).equals(userDTO.getUsername()) && !isTokenExpired(token));
    }
}
