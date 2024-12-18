package com.example.api_gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient; // alternatively netflix ?
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${user-service.id}")
    private String userServiceId;

    @Value("${user-service.uri}")
    private String userServiceUri;

    @Value("${user-service.path}")
    private String userServicePath;

    @Value("${authentication-service.id}")
    private String authenticationServiceId;

    @Value("${authentication-service.uri}")
    private String authenticationServiceUri;

    @Value("${authentication-service.path}")
    private String authenticationServicePath;

    @Value("${task-service.id}")
    private String taskServiceId;

    @Value("${task-service.uri}")
    private String taskServiceUri;

    @Value("${task-service.path}")
    private String taskServicePath;

    @Value("${elasticsearch-service.id}")
    private String elasticsearchServiceId;

    @Value("${elasticsearch-service.uri}")
    private String elasticsearchServiceUri;

    @Value("${elasticsearch-service.path}")
    private String elasticsearchServicePath;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    // Checks authorization and reroutes
    // ToDo : caching, loadbalancer
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String query = exchange.getRequest().getURI().getQuery();
        if (query != null && !query.isEmpty())
            query = query.replace(" ", "%20").replace("|", "%7C");

        // Rerouting
        URI newUri = URI.create(getUri(path) + path + ((query != null && !query.isEmpty()) ? "?" + query : ""));
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, newUri);

        System.out.println("Incoming request URI: " + exchange.getRequest().getURI());
        System.out.println("Resolved to the following URI: " + newUri);
        if (newUri == null) {
            return onError(exchange, "URI could not be resolved");
        }
        // Skip JWT validation for /auth/** routes and registration of new user
        if (path.startsWith("/auth") || (path.equals("/users") && exchange.getRequest().getMethod() == HttpMethod.POST)) {
            return chain.filter(exchange);
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
        String authUri = getUri(authenticationServicePath) + authenticationServicePath + "/validate";
        // Call the authentication service to validate the token
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                authUri,
                HttpMethod.GET,
                new HttpEntity<>(token, headersNew),
                Boolean.class);
        if (responseEntity.getBody() != null && Boolean.TRUE.equals(responseEntity.getBody())) {
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

    private String getServiceIdFromPath(String path) {
        if (path.startsWith(userServicePath)) {
            return userServiceId;
        } else if (path.startsWith(authenticationServicePath)) {
            return authenticationServiceId;
        } else if (path.startsWith(taskServicePath)) {
            return taskServiceId;
        } else if (path.startsWith(elasticsearchServicePath)) {
            return elasticsearchServiceId;
        } else {
            return null;
        }
    }

    private String getStaticServiceUri(String serviceId) {
        if (userServiceId.equals(serviceId)) {
            return userServiceUri;
        } else if (authenticationServiceId.equals(serviceId)) {
            return authenticationServiceUri;
        } else if (taskServiceId.equals(serviceId)) {
            return taskServiceUri;
        } else if (elasticsearchServiceId.equals(serviceId)) {
            return elasticsearchServiceUri;
        } else {
            return null;
        }
    }

    private boolean isServiceInEureka(String serviceId) {
        try {
            List<String> services = discoveryClient.getServices();
            return services.contains(serviceId);
        } catch (Exception e) {
            System.err.println("Error fetching services from Eureka: " + e.getMessage());
            return false;
        }
    }

    private String getServiceUriFromEureka(String serviceId) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        
        // Potentially loadbalancer here in case we have multiple services
        if (!instances.isEmpty()) {
            return instances.get(0).getUri().toString();
        } else {
            return null;
        }
    }

    private String getUri(String path) {
        String serviceId = getServiceIdFromPath(path);
        
        if (isServiceInEureka(serviceId)) {
            return getServiceUriFromEureka(serviceId);
        } else {
            return getStaticServiceUri(serviceId);
        }
    }
}

