package com.example.authentication_service.service;

import com.example.authentication_service.model.UserDTO;

import com.example.authentication_service.config.JWTConfig;
import com.example.authentication_service.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {
    @Autowired
    private JWTConfig jwtConfig;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private RestTemplate restTemplate = new RestTemplate();

    // Rewrite the function via adding : Username->Password API
    public String authenticate(UserDTO userDTO) {
        // Fetch user details from User Service
        String userServiceUrlWithUsername = jwtConfig.getUserServiceAdress() + userDTO.getUsername();
        UserDTO user = restTemplate.getForObject(userServiceUrlWithUsername, UserDTO.class); // Maybe change to responseEntity

        // Check if the user exists and the password is correct
        if (user != null && user.getPassword().equals(userDTO.getPassword())) {
            return jwtTokenUtil.generateToken(user.getUsername());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}