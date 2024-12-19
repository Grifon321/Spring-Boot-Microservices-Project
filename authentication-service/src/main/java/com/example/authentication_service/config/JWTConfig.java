package com.example.authentication_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JWTConfig {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;
    
    @Value("${user-service-adress}")
    private String userServiceAdress;

    // Getters and Setters
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getUserServiceAdress() {
        return userServiceAdress;
    }

    public void setUserServiceAdress(String userServiceAdress) {
        this.userServiceAdress = userServiceAdress;
    }

    // Bean for RestTemplate
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
