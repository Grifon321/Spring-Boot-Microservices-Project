package com.example.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.HashMap;


import com.example.web.model.Task;
import com.example.web.service.ApiService;


@Controller
@RequestMapping("/api")
public class WebController {

    @Autowired
    private ApiService apiService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        // If no username or password, return 400 with a custom error message
        if (username == null || password == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password missing");

        // Authenticate and return JWT token
        String token = apiService.authenticate(username, password);
        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        }

        // If authentication fails, return 401 with a custom error message
        String errorMessage = "Invalid login request";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String email = payload.get("email");
        String password = payload.get("password");

        // If no username or password, return 400 with a custom error message
        if (username == null || password == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password missing");
        
        // Create a new user or return 400 with a custom error message
        String responce = apiService.signup(username, password, email);
        if (responce == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User creation failed");

        // Authenticate and return JWT token
        String token = apiService.authenticate(username, password);
        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } 
        
        // If authentication fails, return 401 Bad Request with a custom error message
        String errorMessage = "Invalid signup request";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
    }

    @GetMapping("/content")
    public ResponseEntity<List<Task>> contentPage(@RequestParam String token, @RequestParam String username) {
        // Retrieve all tasks and send 200 response with tasks in body
        List<Task> contents = apiService.getAllTasksByUsername(token, username);
        return ResponseEntity.ok(contents);
    }
    
    @PostMapping("/createTask")
    public ResponseEntity<String> createTask(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String username = payload.get("username");
        String name = payload.get("name");
        String text = payload.get("text");
        String deadline = payload.get("deadline");
        String status = payload.get("status");

        // Create the task and send 201 response, otherwise 400 response
        if (token != null && username != null && name != null && text != null) {
            apiService.createNewTask(token, username, name, text, deadline, status);
            return ResponseEntity.status(HttpStatus.CREATED).body("Task has been created");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username, Token, Name, Text, Deadline or Status missing");
        }
            
    }

    @DeleteMapping("/deleteTask")
    public ResponseEntity<String> deleteTask(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String id = payload.get("id");

        // Delete the task and send 204 response, otherwise 400 response
        if (token != null && id != null) {
            apiService.deleteTask(id, token);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token or ID missing");
        }
    }

    @PutMapping("/editTask")
    public ResponseEntity<String> editTask(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");
        Long id = Long.valueOf((Integer) payload.get("id"));
        String name = (String) payload.get("name");
        String text = (String) payload.get("text");
        String deadline = (String) payload.get("deadline");
        String status = (String) payload.get("status");
        List<Long> userIds = (List<Long>) payload.get("userIds");

        // Edit the task and send 200 request, otherwise 400 request
        if (token != null && id != null) {
            apiService.editTask(token, id, name, text, deadline, status, userIds);
            return ResponseEntity.status(HttpStatus.OK).body("Task has been updated");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token or ID missing");
        }
            
    }

    @PostMapping("/addUserId")
    public ResponseEntity<String> addUserId(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");
        Long id = Long.valueOf((Integer) payload.get("id"));
        Long userId = Long.valueOf(String.valueOf(payload.get("userId")));

        // Add the user to the task and send 200 response, otherwise 400 response
        if (token != null && id != null && userId != null) {
            apiService.addUserId(id, userId, token);
            return ResponseEntity.status(HttpStatus.OK).body("Task has been updated");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token, ID or User ID missing");
        } 
    }

    @PostMapping("/removeUserId")
    public ResponseEntity<String> removeUserId(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");
        Long id = Long.valueOf((Integer) payload.get("id"));
        Long userId = Long.valueOf(String.valueOf(payload.get("userId")));

        // Remove the user to the task and send 200 response, otherwise 400 response
        if (token != null && id != null && userId != null) {
            apiService.removeUserId(id, userId, token);
            return ResponseEntity.status(HttpStatus.OK).body("Task has been updated");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token, ID or User ID missing");
        } 
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchContents(@RequestParam String query, @RequestParam String token, @RequestParam String username) {
        // Perform a search query and return contents with 200 response
        List<Task> contents = apiService.searchByUsernameQuery(token, username, query);
        return ResponseEntity.ok(contents);
    }
}
