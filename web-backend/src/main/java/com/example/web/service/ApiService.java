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

@Service
public class ApiService {

    private final RestTemplate restTemplate;

    @Value("${api.gateway.url}")
    private String apiGatewayUrl;

    
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    

    public String signup(String username, String password, String email) {
        String url = apiGatewayUrl + "/users";
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
        
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

    public Long getUserId(String token, String username) {
        String url = apiGatewayUrl + "/users/id-by-username/" + username;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long id = restTemplate.exchange(url, HttpMethod.GET, entity, Long.class).getBody();
        return id;
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

    public void createNewTask(String token, String username, String name, String text, String deadline, String status) {
        Long userId = getUserId(token, username);
        String requestPayload = "{\"name\":\"" + name + "\",\"text\":\"" + text + "\",\"deadline\":\"" + deadline + "\",\"status\":\"" + status +  "\",\"userIds\":[" + userId +"]}";

        String url = apiGatewayUrl + "/tasks";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        restTemplate.postForObject(url, entity, String.class);
    }

    public void editTask(String token, Long id, String name, String text, String deadline, String status, List<Long> userIds) {
        StringBuilder userIdsStringBuilder = new StringBuilder("[");
        for (int i = 0; i < userIds.size(); i++) {
            userIdsStringBuilder.append(userIds.get(i));
            if (i < userIds.size() - 1) {
                userIdsStringBuilder.append(",");
            }
        }
        userIdsStringBuilder.append("]");

        String requestPayload = "{\"name\":\"" + name + "\",\"text\":\"" + text + "\",\"deadline\":\"" + deadline + "\",\"status\":\"" + status + "\",\"userIds\":" + userIdsStringBuilder.toString() + "}";

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

    public void addUserId(Long id, Long userId, String token) {
        String url = apiGatewayUrl + "/tasks/" + id + "?userId=" + userId + "&action=" + "add";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.PATCH, entity, new ParameterizedTypeReference<Task>() {});
    }

    public void removeUserId(Long id, Long userId, String token) {
        String url = apiGatewayUrl + "/tasks/" + id + "?userId=" + userId + "&action=" + "remove";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.PATCH, entity, new ParameterizedTypeReference<Task>() {});
    }
}