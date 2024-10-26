package com.example.authentication_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.authentication_service.config.JWTConfig;
import com.example.authentication_service.model.UserDTO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JWTConfig jwtConfig;
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        String username = null;
        
        // Extract the token from the header
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
            username = jwtTokenUtil.extractUsername(token);
        }
    
        // Check if the user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String userServiceUrlWithUsername = jwtConfig.getUserServiceAdress() + username;
            UserDTO userDTO = restTemplate.getForObject(userServiceUrlWithUsername, UserDTO.class); // Maybe change to responseEntity
    
            // Validate the token
            if (token != null && jwtTokenUtil.validateToken(token, userDTO)) {
                // Create the authentication object without authorities
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDTO, null, new ArrayList<>());
    
                // Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        chain.doFilter(request, response);
    }
}