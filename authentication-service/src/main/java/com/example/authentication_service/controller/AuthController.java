package com.example.authentication_service.controller;

import com.example.authentication_service.model.UserDTO;
import com.example.authentication_service.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody UserDTO userDTO) {
        String JWTToken = authService.authenticate(userDTO);
        if (JWTToken == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT token could not be created.");
        return ResponseEntity.ok(JWTToken);
    }

    
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String token = null;

        // Check if the Authorization header contains a bearer token
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7); // Extract the token without 'Bearer '
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // No Bearer Token present
        }

        // Validate token
        if (authService.validateToken(token))
            return ResponseEntity.ok(true);

        // If token is not valid
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // Invalid token
    }

}