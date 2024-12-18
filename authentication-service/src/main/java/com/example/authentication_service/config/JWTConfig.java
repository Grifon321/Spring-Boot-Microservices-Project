package com.example.authentication_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

@Configuration
@Data
public class JWTConfig {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;
    
    @Value("${user-service-adress}")
    private String userServiceAdress;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
