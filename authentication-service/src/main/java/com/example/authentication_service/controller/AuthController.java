package com.example.authentication_service.controller;

import com.example.authentication_service.config.JWTConfig;
import com.example.authentication_service.model.UserDTO;
import com.example.authentication_service.security.JwtTokenUtil;
import com.example.authentication_service.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JWTConfig jwtConfig;
    private RestTemplate restTemplate = new RestTemplate();


    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authService.authenticate(userDTO));
    }

    
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(HttpServletRequest request) {
        
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Check if the Authorization header contains a bearer token
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7); // Extract the token without 'Bearer '
            // username = jwtTokenUtil.extractUsername(token);
        }

        // Validate token
        if (authService.validateToken(token)) {
            return ResponseEntity.ok(true);
        }
        /*
        if (token != null && username != null && !jwtTokenUtil.isTokenExpired(token)) {
            return ResponseEntity.ok(true);  // Token is valid
            
            String userServiceUrlWithUsername = jwtConfig.getUserServiceAdress() + username;
            UserDTO userDTO = restTemplate.getForObject(userServiceUrlWithUsername, UserDTO.class);
            if (jwtTokenUtil.validateToken(token, userDTO)) {
                return ResponseEntity.ok(true);  // Token is valid
            }
            
            
        }
        */

        // If token is not valid
        return ResponseEntity.ok(false);
    }

}