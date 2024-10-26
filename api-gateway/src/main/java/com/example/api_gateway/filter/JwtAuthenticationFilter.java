package com.example.api_gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${spring.cloud.gateway.routes[1].uri}")
    private String gatewayUri;
    private String validateServerAdress;

    @PostConstruct
    public void init() {
        validateServerAdress = gatewayUri + "/auth/validate";
    }

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip JWT validation for /auth/** routes and /users/register
        if (path.startsWith("/auth") ||path.startsWith("/users/register")) {
            return chain.filter(exchange); // Skip further processing
        }

        // Check for Authorization header
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Missing Authorization header");
        }

        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Invalid Authorization header");
        }

        String token = authHeader.substring(7);

        HttpHeaders headersNew = new HttpHeaders();
        headersNew.set("AUTHORIZATION", "Bearer " + token);
        // Call the authentication service to validate the token
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                validateServerAdress,
                HttpMethod.POST,
                new HttpEntity<>(token, headersNew),
                Boolean.class);
        if (responseEntity.getBody() != null && responseEntity.getBody()) {
            return chain.filter(exchange); // Continue with the request if valid
        } else {
            return onError(exchange, "Invalid token");
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error) {
        // Set error response with an empty body
        exchange.getResponse().setRawStatusCode(401); // Set status code to 401
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(error.getBytes(StandardCharsets.UTF_8));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

