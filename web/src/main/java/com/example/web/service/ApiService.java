package com.example.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import com.example.web.model.Task;
import com.example.web.model.User;

@Service
public class ApiService {

    private final RestTemplate restTemplate;

    @Value("${api.gateway.url}")
    private String apiGatewayUrl;

    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public String signup(String username, String password, String email) {
        String url = apiGatewayUrl + "/users/register";
        String requestPayload = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        return restTemplate.postForObject(url, entity, String.class);
    }

    public String authenticate(String username, String password) {
        String url = apiGatewayUrl + "/auth/authenticate";
        String requestPayload = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        return restTemplate.postForObject(url, entity, String.class);
    }

    public String validateToken(String token) {
        String url = apiGatewayUrl + "/auth/validate";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.postForObject(url, entity, String.class);
    }

    public Long getUserId(String token, String username) {
        String url = apiGatewayUrl + "/users/username/" + username;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        User user = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<User>() {}).getBody();
        return user.id;
    }
 
    
    public List<Task> getAllTasksByUsername(String token, String username) {
        Long userId = getUserId(token, username);
        
        String url = apiGatewayUrl + "/tasks/user/" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Task>>() {}).getBody();
    }

    public List<Task> searchByUsernameQuery(String token, String username, String query) {
        Long userId = getUserId(token, username);
        
        String url = apiGatewayUrl + "/elastic/" + userId + "/search?query=" + query;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Task>>() {}).getBody();
    }

    public void createNewTask(String token, String username, String name, String text, String deadline) {
        Long userId = getUserId(token, username);
        String requestPayload = "{\"name\":\"" + name + "\",\"text\":\"" + text + "\",\"deadline\":\"" + deadline +  "\",\"userIds\":[" + userId +"]}";

        String url = apiGatewayUrl + "/tasks/register";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        restTemplate.postForObject(url, entity, String.class);
    }

    public void editTask(String id, String token, String username, String name, String text, String deadline) {
        Long userId = getUserId(token, username);
        String requestPayload = "{\"name\":\"" + name + "\",\"text\":\"" + text + "\",\"deadline\":\"" + deadline +  "\",\"userIds\":[" + userId +"]}";

        String url = apiGatewayUrl + "/tasks/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, new ParameterizedTypeReference<Task>() {});
    }

    public void deleteTask(String id,String token) {
        String url = apiGatewayUrl + "/tasks/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, new ParameterizedTypeReference<Task>() {});
    }
}

    
