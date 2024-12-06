package com.example.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseBody; // For @ResponseBody annotation
import java.util.Map; // For Map interface
import java.util.HashMap; // For HashMap implementation


import com.example.web.model.Task;
import com.example.web.service.ApiService;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/api")
public class WebController {

    @Autowired
    private ApiService apiService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || password == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password missing");

        String token = apiService.authenticate(username, password);

        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } // ToDo : fix if not authorized correctly : 403 request

        // If authentication fails, return 400 Bad Request with a custom error message
        String errorMessage = "Invalid login request";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String email = payload.get("email");
        String password = payload.get("password");

        if (username == null || password == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password missing");
        
        apiService.signup(username, password, email);
        String token = apiService.authenticate(username, password);
        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } // ToDo : fix if not authorized correctly : 403 request
        
        // If authentication fails, return 400 Bad Request with a custom error message
        String errorMessage = "Invalid signup request";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @GetMapping("/content")
    @ResponseBody
    public ResponseEntity<?> contentPage(@RequestParam String token, @RequestParam String username) {
        try {
            List<Task> contents = apiService.getAllTasksByUsername(token, username);
            return ResponseEntity.ok(contents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching data");
        }
    }
    
    @PostMapping("/createTask")
    public void createTask(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String username = payload.get("username");
        String name = payload.get("name");
        String text = payload.get("text");
        String deadline = payload.get("deadline");
        if (token != null && username != null && name != null && text != null)
            apiService.createNewTask(token, username, name, text, deadline);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchContents(@RequestParam String query, @RequestParam String token, @RequestParam String username) {
        try {
            List<Task> contents = apiService.searchByUsernameQuery(token, username, query);
            return ResponseEntity.ok(contents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching data");
        }

    }
}
