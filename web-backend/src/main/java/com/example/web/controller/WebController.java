package com.example.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseBody;
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
    public ResponseEntity<String> createTask(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String username = payload.get("username");
        String name = payload.get("name");
        String text = payload.get("text");
        String deadline = payload.get("deadline");
        String status = payload.get("status");

        if (token != null && username != null && name != null && text != null) {
            apiService.createNewTask(token, username, name, text, deadline, status);
            
            return ResponseEntity.ok( "{\"success\": true}");
        } else {
            return ResponseEntity.badRequest().body( "{\"success\": false}");
        }
            
    }

    @PostMapping("/deleteTask")
    public ResponseEntity<String> deleteTask(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String id = payload.get("id");

        if (token != null && id != null) {
            apiService.deleteTask(id, token);
            
            return ResponseEntity.ok( "{\"success\": true}");
        } else {
            return ResponseEntity.badRequest().body( "{\"success\": false}");
        }
    }

    @PostMapping("/editTask")
    public ResponseEntity<String> editTask(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");
        Long id = Long.valueOf((Integer) payload.get("id"));
        String name = (String) payload.get("name");
        String text = (String) payload.get("text");
        String deadline = (String) payload.get("deadline");
        String status = (String) payload.get("status");
        List<Long> userIds = (List<Long>) payload.get("userIds");

        if (token != null && id != null) {
            apiService.editTask(token, id, name, text, deadline, status, userIds);
            return ResponseEntity.ok( "{\"success\": true}");
        } else {
            return ResponseEntity.badRequest().body( "{\"success\": false}");
        }
            
    }

    @PutMapping("/addUserId")
    public ResponseEntity<String> addUserId(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");
        Long id = Long.valueOf((Integer) payload.get("id"));
        Long userId = Long.valueOf(String.valueOf(payload.get("userId")));

        if (token != null && id != null && userId != null) {
            apiService.addUserId(id, userId, token);
            return ResponseEntity.ok( "{\"success\": true}");
        } else {
            return ResponseEntity.badRequest().body( "{\"success\": false}");
        } 
    }

    @PutMapping("/removeUserId")
    public ResponseEntity<String> removeUserId(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");
        Long id = Long.valueOf((Integer) payload.get("id"));
        Long userId = Long.valueOf(String.valueOf(payload.get("userId")));

        if (token != null && id != null && userId != null) {
            apiService.removeUserId(id, userId, token);
            return ResponseEntity.ok( "{\"success\": true}");
        } else {
            return ResponseEntity.badRequest().body( "{\"success\": false}");
        } 
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
