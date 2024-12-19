package com.example.authentication_service.service;

import com.example.authentication_service.model.UserDTO;

import com.example.authentication_service.config.JWTConfig;
import com.example.authentication_service.security.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {
    @Autowired
    private JWTConfig jwtConfig;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RestTemplate restTemplate;

    public String authenticate(UserDTO userDTO) {
        // Fetch user details from User Service
        String userServiceUrlWithUsername = jwtConfig.getUserServiceAdress() + "/password/" + userDTO.getUsername();
        String password = restTemplate.getForObject(userServiceUrlWithUsername, String.class);

        // Check if the user exists and the password is correct
        if (password != null && password.equals(userDTO.getPassword())) {
            return jwtTokenUtil.generateToken(userDTO.getUsername());
        } else {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            if (jwtTokenUtil.isTokenExpired(token))
                return false;
            return true;
        } catch (ExpiredJwtException e) {
            return false; //extractClaims() throws ExpiredJwtException when token is expired
        }
    }
}